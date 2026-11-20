package wottrich.github.io.smartchecklist.share.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.SerializationException
import wottrich.github.io.smartchecklist.serversync.api.ServerErrorResponse
import wottrich.github.io.smartchecklist.serversync.api.ServerSyncJson
import wottrich.github.io.smartchecklist.share.api.HealthResponse
import wottrich.github.io.smartchecklist.share.api.PairRequest
import wottrich.github.io.smartchecklist.share.api.PairResponse
import wottrich.github.io.smartchecklist.share.api.ShareAcceptResponse
import wottrich.github.io.smartchecklist.share.api.ShareOffersResponse
import wottrich.github.io.smartchecklist.share.domain.ShareError
import java.io.IOException

/**
 * Guest-side client for the share-only endpoints of a device host
 * (`share-contract.md`). Push/pull reuse the shared [wottrich.github.io.smartchecklist.serversync.api.ServerApi]
 * — this covers `/health`, `/v1/share/offers`, `/v1/auth/register` and
 * `/v1/share/{shareId}/accept`, mapping failures to [ShareError].
 *
 * @param address `ip:port` of the host (plain HTTP — LAN-only, user-configured).
 */
class ShareHostClient(private val client: HttpClient) {

    suspend fun health(address: String): HealthResponse = readSuccess(address) {
        client.get(url(address, PATH_HEALTH))
    }

    suspend fun fetchOffers(address: String, token: String?): ShareOffersResponse = readSuccess(address) {
        client.get(url(address, PATH_OFFERS)) {
            token?.let { header("Authorization", "Bearer $it") }
        }
    }

    /** Pairs with the host using the owner's displayed code (proof-of-knowledge). */
    suspend fun pair(address: String, request: PairRequest): PairResponse = readSuccess(address) {
        client.post(url(address, PATH_PAIR)) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun accept(address: String, token: String, shareId: String): ShareAcceptResponse =
        readSuccess(address) {
            client.post(url(address, "$PATH_ACCEPT_PREFIX/$shareId/accept")) {
                header("Authorization", "Bearer $token")
            }
        }

    // ---------- helpers ----------

    private suspend inline fun <reified T> readSuccess(address: String, block: () -> io.ktor.client.statement.HttpResponse): T {
        val response = try {
            block()
        } catch (exception: IOException) {
            throw ShareError.HostUnreachable(exception)
        }
        if (response.status.isSuccess()) {
            return try {
                response.body()
            } catch (exception: SerializationException) {
                throw ShareError.ValidationRejected("host answered with an unexpected payload")
            }
        }
        val body = runCatching { response.bodyAsText() }.getOrNull().orEmpty()
        val error = runCatching { ServerSyncJson.decodeFromString<ServerErrorResponse>(body) }.getOrNull()
        throw when {
            // code-based mapping first — 401/403 carry several distinct v2 kinds
            error?.code == "pairing_required" -> ShareError.PairingRequired()
            error?.code == "device_revoked" -> ShareError.DeviceRevoked()
            error?.code == "token_expired" -> ShareError.TokenExpired()
            error?.code == "too_many_attempts" -> ShareError.PairingFailed(retryLater = true)
            error?.code == "pairing_failed" -> ShareError.PairingFailed(retryLater = false)
            response.status == HttpStatusCode.Unauthorized -> ShareError.Unauthenticated()
            response.status == HttpStatusCode.Forbidden -> ShareError.ShareNotAccepted()
            response.status == HttpStatusCode.NotFound -> ShareError.ShareNotFound()
            response.status == HttpStatusCode.UnprocessableEntity ->
                ShareError.ValidationRejected(error?.message ?: "validation failed")
            else -> ShareError.HostUnreachable(
                IllegalStateException("HTTP ${response.status.value} from $address")
            )
        }
    }

    private fun url(address: String, path: String) = "http://${address.trim()}/$path"

    private companion object {
        const val PATH_HEALTH = "health"
        const val PATH_OFFERS = "v1/share/offers"
        const val PATH_PAIR = "v1/pair"
        const val PATH_ACCEPT_PREFIX = "v1/share"
    }
}
