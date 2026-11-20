package wottrich.github.io.smartchecklist.share.host

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.header
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import wottrich.github.io.smartchecklist.serversync.api.ServerErrorResponse
import wottrich.github.io.smartchecklist.serversync.api.ServerSyncJson
import wottrich.github.io.smartchecklist.serversync.api.SyncPushBody
import wottrich.github.io.smartchecklist.share.api.HealthResponse
import wottrich.github.io.smartchecklist.share.api.PairRequest
import wottrich.github.io.smartchecklist.share.api.ShareOfferDto
import wottrich.github.io.smartchecklist.share.api.ShareOffersResponse
import wottrich.github.io.smartchecklist.share.domain.ShareError
import java.net.ServerSocket

/**
 * The embedded host server — a thin CIO shell over [ShareHostService], serving
 * `docs/api/share-contract.md` on the LAN. Foreground-only lifecycle for v1: the
 * owner's app process being alive **is** the hosting guarantee; guests queue
 * offline via their outbox until it comes back.
 */
class EmbeddedShareServer(
    private val service: ShareHostService,
    private val prefs: ShareHostPrefs,
) {

    private var engine: EmbeddedServer<*, *>? = null

    val isRunning: Boolean get() = engine != null

    /**
     * Binds on [port] at [host]. Callers ([ShareHostManager]) pick the Wi-Fi
     * interface address (v2: never `0.0.0.0`) and probe availability first,
     * walking the 8090→8100 range; the returned port is the one actually bound.
     */
    fun start(port: Int, host: String): Int {
        check(engine == null) { "share host already running" }
        val application: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration> =
            embeddedServer(CIO, host = host, port = port) {
            install(ContentNegotiation) {
                json(ServerSyncJson)
            }
            routing {
                get(HEALTH) {
                    call.respond(HealthResponse(status = "ok", role = ROLE_DEVICE_HOST, proto = WIRE_PROTO))
                }
                get(OFFERS) {
                    // v2: offers are gated — an unpaired caller learns nothing but
                    // "a host exists here" (403 pairing_required, no names/ids).
                    val token = call.bearerToken()
                    if (token.isNullOrBlank()) {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            ServerErrorResponse(code = ShareError.PairingRequired().kind, message = "pairing required"),
                        )
                        return@get
                    }
                    authorizeOrRespond(call) { _ ->
                        val offers = service.offers()
                        ShareOffersResponse(
                            offers = offers.map {
                                ShareOfferDto(
                                    shareId = it.shareId,
                                    name = it.name,
                                    ownerDeviceName = it.ownerDeviceName,
                                    updatedAt = it.updatedAt,
                                )
                            }
                        )
                    }
                }
                post(PAIR) {
                    val body = call.receive<PairRequest>()
                    call.respond(HttpStatusCode.Created, service.pair(body))
                }
                post(ACCEPT) {
                    authorizeOrRespond(call) { userId ->
                        service.accept(userId, call.parameters["shareId"].orEmpty())
                    }
                }
                get(PULL) {
                    authorizeOrRespond(call) { _ ->
                        val response = service.pull(
                            token = call.bearerToken(),
                            shareId = call.request.queryParameters["shareId"],
                            since = call.request.queryParameters["since"]?.toLongOrNull() ?: 0L,
                            limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: DEFAULT_PAGE_SIZE,
                        )
                        // v2: body travels as an encrypted envelope (share-contract.md)
                        envelopeContent(ServerSyncJson.encodeToString(response), aad = "pull")
                    }
                }
                post(PUSH) {
                    authorizeOrRespond(call) { _ ->
                        val envelope = call.receiveText()
                        val key = ShareCrypto.contentKey(prefs.getMasterPairingSecret())
                        val plain = if (envelope.length > MAX_BODY_CHARS) {
                            null
                        } else {
                            ShareCrypto.open(envelope, key, aad = "push")
                        }
                        if (plain == null) {
                            // 413 / 422: body cap or unreadable envelope (v2 caps)
                            TextContent(
                                ServerSyncJson.encodeToString(
                                    ServerErrorResponse(
                                        code = if (envelope.length > MAX_BODY_CHARS) "too_large" else "validation",
                                        message = "rejected push",
                                    )
                                ),
                                ContentType.Application.Json,
                                HttpStatusCode.PayloadTooLarge,
                            )
                        } else {
                            val body = ServerSyncJson.decodeFromString<SyncPushBody>(plain.decodeToString())
                            val response = service.push(token = call.bearerToken(), changes = body.changes)
                            envelopeContent(ServerSyncJson.encodeToString(response), aad = "push")
                        }
                    }
                }
            }
        }
        application.start(wait = false)
        engine = application
        return port
    }

    fun stop() {
        engine?.stop(STOP_GRACE_MS, STOP_TIMEOUT_MS)
        engine = null
    }

    /** True when [port] can still be bound on this device. */
    fun isPortAvailable(port: Int): Boolean = try {
        ServerSocket(port).use { true }
    } catch (ignored: Exception) {
        false
    }

    // ---------- helpers ----------

    /** Encrypts [jsonBody] under the host content key as the envelope content. */
    private fun envelopeContent(jsonBody: String, aad: String): TextContent {
        val key = ShareCrypto.contentKey(prefs.getMasterPairingSecret())
        return TextContent(ShareCrypto.seal(jsonBody.toByteArray(), key, aad), ContentType.Application.Json)
    }

    /** Raw header value with the `Bearer ` prefix stripped — the token proper. */
    private fun ApplicationCall.bearerToken(): String? =
        request.header(AUTHORIZATION)?.removePrefix("Bearer ")?.trim()

    /**
     * Validates the bearer token (401 in-place when bad), then serves the block,
     * mapping [ShareError]s to their wire status codes (share-contract.md).
     */
    private suspend fun authorizeOrRespond(
        call: ApplicationCall,
        block: suspend (userId: String) -> Any,
    ) {
        val token = call.bearerToken()
        val userId = try {
            service.authenticate(token)
        } catch (error: ShareError.Unauthenticated) {
            call.respond(HttpStatusCode.Unauthorized, ServerErrorResponse(code = error.kind, message = error.message))
            return
        }
        try {
            call.respond(block(userId))
        } catch (error: ShareError) {
            call.respond(error.toStatus(), ServerErrorResponse(code = error.kind, message = error.message))
        }
    }

    private fun ShareError.toStatus(): HttpStatusCode = when (this) {
        is ShareError.ShareNotFound -> HttpStatusCode.NotFound
        is ShareError.ShareNotAccepted -> HttpStatusCode.Forbidden
        is ShareError.PairingRequired -> HttpStatusCode.Forbidden
        is ShareError.DeviceRevoked -> HttpStatusCode.Forbidden
        is ShareError.PairingFailed ->
            if (retryLater) HttpStatusCode.TooManyRequests else HttpStatusCode.UnprocessableEntity
        is ShareError.TooManyRequests -> HttpStatusCode.TooManyRequests
        is ShareError.TokenExpired -> HttpStatusCode.Unauthorized
        is ShareError.Unauthenticated -> HttpStatusCode.Unauthorized
        is ShareError.ValidationRejected -> HttpStatusCode.UnprocessableEntity
        is ShareError.HostUnreachable -> HttpStatusCode.InternalServerError
    }

    private companion object {
        const val HEALTH = "/health"
        const val OFFERS = "/v1/share/offers"
        const val PAIR = "/v1/pair"
        const val ACCEPT = "/v1/share/{shareId}/accept"
        const val PULL = "/v1/sync/pull"
        const val PUSH = "/v1/sync/push"
        const val AUTHORIZATION = "Authorization"
        const val ROLE_DEVICE_HOST = "device-host"
        const val WIRE_PROTO = 2
        const val DEFAULT_PAGE_SIZE = 500
        /** 512 KB body → base64 inflates ~4/3. */
        const val MAX_BODY_CHARS = 700_000
        const val STOP_GRACE_MS = 500L
        const val STOP_TIMEOUT_MS = 1_000L
    }
}
