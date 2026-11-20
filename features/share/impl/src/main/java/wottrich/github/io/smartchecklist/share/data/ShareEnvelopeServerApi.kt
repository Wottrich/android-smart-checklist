package wottrich.github.io.smartchecklist.share.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.SerializationException
import wottrich.github.io.smartchecklist.serversync.api.BackupStatusResponse
import wottrich.github.io.smartchecklist.serversync.api.MeResponse
import wottrich.github.io.smartchecklist.serversync.api.RegisterResponse
import wottrich.github.io.smartchecklist.serversync.api.ServerApi
import wottrich.github.io.smartchecklist.serversync.api.ServerErrorResponse
import wottrich.github.io.smartchecklist.serversync.api.ServerSyncJson
import wottrich.github.io.smartchecklist.serversync.api.SyncPullResponse
import wottrich.github.io.smartchecklist.serversync.api.SyncPushBody
import wottrich.github.io.smartchecklist.serversync.api.SyncPushResponse
import wottrich.github.io.smartchecklist.serversync.domain.SyncError
import wottrich.github.io.smartchecklist.share.host.ShareCrypto
import wottrich.github.io.smartchecklist.share.host.ShareHostPrefs

/**
 * `ServerApi` for **device hosts only** (v2 hardening): pull/push bodies travel as
 * the AES-256-GCM envelope of `ShareCrypto` under the host content key — derived
 * from the pairing secret the guest stored at pair time. Everything the PC
 * backend uses (`register`, `me`, backup endpoints) is not served by device
 * hosts and throws — the share flows never call it.
 *
 * Wrapping a `ServerApi` implementation (rather than decorating the PC-backend
 * one) keeps the PC-backend client byte-for-byte v1.
 */
class ShareEnvelopeServerApi(
    private val client: HttpClient,
    private val prefs: ShareHostPrefs,
) : ServerApi {

    override suspend fun register(baseUrl: String, deviceName: String?): RegisterResponse =
        unsupported()

    override suspend fun me(baseUrl: String, token: String): MeResponse = unsupported()

    override suspend fun backupStatus(baseUrl: String, token: String): BackupStatusResponse = unsupported()

    override suspend fun downloadBackup(baseUrl: String, token: String): String = unsupported()

    override suspend fun uploadBackup(baseUrl: String, token: String, backupJson: String) = unsupported()

    override suspend fun push(baseUrl: String, token: String, body: SyncPushBody): SyncPushResponse {
        val key = contentKeyOrThrow(baseUrl)
        val envelope = ShareCrypto.seal(
            ServerSyncJson.encodeToString(body).toByteArray(),
            key,
            aad = AAD_PUSH,
        )
        val response = client.post(url(baseUrl, PATH_PUSH)) {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(envelope)
        }
        val plain = decodeEnvelopeOrThrow(response, key, AAD_PUSH)
        return decodeOrThrow(plain)
    }

    override suspend fun pull(
        baseUrl: String,
        token: String,
        since: Long,
        limit: Int,
        shareId: String?
    ): SyncPullResponse {
        val key = contentKeyOrThrow(baseUrl)
        val shareQuery = shareId?.let { "&shareId=$it" }.orEmpty()
        val response = client.get(url(baseUrl, PATH_PULL) + "?since=$since&limit=$limit$shareQuery") {
            header("Authorization", "Bearer $token")
        }
        val plain = decodeEnvelopeOrThrow(response, key, AAD_PULL)
        val decoded = decodeOrThrow<SyncPullResponse>(plain)
        // v2 hardening: never blindly trust a host — sanity-caps before applying
        if (decoded.changes.size > MAX_PAGE_CHANGES) {
            throw SyncError.ValidationRejected("host page too large (${decoded.changes.size} changes)")
        }
        decoded.changes.firstOrNull { it.payload.toString().length > MAX_ROW_PAYLOAD_CHARS }?.let {
            throw SyncError.ValidationRejected("host row payload too large for ${it.uuid}")
        }
        return decoded
    }

    // ---------- helpers ----------

    private fun contentKeyOrThrow(baseUrl: String): ByteArray {
        val address = baseUrl.removePrefix("http://").removePrefix("https://").trim()
        val secret = prefs.getGuestPairingSecret(address)
            ?: throw SyncError.ShareRevoked() // no stored pairing secret → must re-pair
        return ShareCrypto.contentKey(secret)
    }

    private suspend fun decodeEnvelopeOrThrow(response: HttpResponse, key: ByteArray, aad: String): ByteArray {
        if (!response.status.isSuccess()) throw toSyncError(response)
        val envelope = try {
            response.body<String>()
        } catch (exception: SerializationException) {
            throw SyncError.Unknown(exception)
        }
        return ShareCrypto.open(envelope, key, aad)
            ?: throw SyncError.Unknown(IllegalStateException("unreadable envelope from host"))
    }

    private inline fun <reified T> decodeOrThrow(plain: ByteArray): T = try {
        ServerSyncJson.decodeFromString(plain.decodeToString())
    } catch (exception: SerializationException) {
        throw SyncError.Unknown(exception)
    }

    private suspend fun toSyncError(response: HttpResponse): SyncError {
        val status = response.status.value
        val body = runCatching { response.bodyAsText() }.getOrDefault("")
        val error = runCatching { ServerSyncJson.decodeFromString<ServerErrorResponse>(body) }.getOrNull()
        return when {
            error?.code == "device_revoked" -> SyncError.ShareRevoked()
            error?.code == "share_not_found" -> SyncError.ShareNotFound()
            status == HttpStatusCode.Unauthorized.value -> SyncError.ServerUnauthorized()
            status == HttpStatusCode.Forbidden.value -> SyncError.ShareRevoked()
            status == HttpStatusCode.UnprocessableEntity.value ->
                SyncError.ValidationRejected(error?.message ?: "validation failed")
            else -> SyncError.Unknown(IllegalStateException("HTTP $status: ${error?.code ?: "no code"}"))
        }
    }

    private fun url(baseUrl: String, path: String) = "${baseUrl.trimEnd('/')}/$path"

    private companion object {
        const val PATH_PULL = "v1/sync/pull"
        const val PATH_PUSH = "v1/sync/push"
        const val AAD_PULL = "pull"
        const val AAD_PUSH = "push"

        /** Sanity caps (v2): a hostile host cannot flood the guest Room DB. */
        const val MAX_PAGE_CHANGES = 500
        const val MAX_ROW_PAYLOAD_CHARS = 256 * 1024

        private fun unsupported(): Nothing =
            throw SyncError.Unknown(IllegalStateException("device hosts do not serve this endpoint"))
    }
}
