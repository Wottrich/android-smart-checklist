package wottrich.github.io.smartchecklist.serversync.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.SerializationException
import wottrich.github.io.smartchecklist.serversync.domain.SyncError
import java.io.IOException

/**
 * Raw HTTP surface of the backend (`smart-checklist-backend`, contract v1).
 * Every method maps failures to [SyncError]; transport problems (refused,
 * timeout, DNS) become [SyncError.ServerUnreachable].
 */
interface ServerApi {

    suspend fun register(baseUrl: String, deviceName: String?): RegisterResponse

    suspend fun me(baseUrl: String, token: String): MeResponse

    suspend fun push(baseUrl: String, token: String, body: SyncPushBody): SyncPushResponse

    suspend fun pull(baseUrl: String, token: String, since: Long, limit: Int): SyncPullResponse

    /** @throws SyncError.NoBackupFound when the server has no backup stored. */
    suspend fun downloadBackup(baseUrl: String, token: String): String

    suspend fun uploadBackup(baseUrl: String, token: String, backupJson: String)

    suspend fun backupStatus(baseUrl: String, token: String): BackupStatusResponse
}

internal class ServerApiImpl(
    private val client: HttpClient
) : ServerApi {

    override suspend fun register(baseUrl: String, deviceName: String?): RegisterResponse {
        return postJson(
            url = url(baseUrl, PATH_REGISTER),
            token = null,
            body = RegisterBody(deviceName),
        )
    }

    override suspend fun me(baseUrl: String, token: String): MeResponse {
        return getJson(url(baseUrl, PATH_ME), token)
    }

    override suspend fun push(baseUrl: String, token: String, body: SyncPushBody): SyncPushResponse {
        return postJson(url(baseUrl, PATH_PUSH), token, body)
    }

    override suspend fun pull(
        baseUrl: String,
        token: String,
        since: Long,
        limit: Int
    ): SyncPullResponse {
        return getJson(url(baseUrl, PATH_PULL) + "?since=$since&limit=$limit", token)
    }

    override suspend fun downloadBackup(baseUrl: String, token: String): String {
        return getBodyOrThrow(
            block = { client.get(url(baseUrl, PATH_BACKUP)) { authorize(token) } },
            notFoundIsNoBackup = true
        )
    }

    override suspend fun uploadBackup(baseUrl: String, token: String, backupJson: String) {
        val response = client.post(url(baseUrl, PATH_BACKUP)) {
            authorize(token)
            contentType(ContentType.Application.Json)
            setBody(backupJson)
        }
        checkSuccess(response, notFoundIsNoBackup = true)
    }

    override suspend fun backupStatus(baseUrl: String, token: String): BackupStatusResponse {
        return getJson(url(baseUrl, "$PATH_BACKUP/status"), token)
    }

    // ---------- helpers ----------

    private suspend inline fun <reified T> getJson(url: String, token: String): T =
        getBodyOrThrow(
            block = { client.get(url) { authorize(token) } },
            notFoundIsNoBackup = false
        )

    private suspend inline fun <reified T> postJson(url: String, token: String?, body: Any): T {
        val response = client.post(url) {
            token?.let { authorize(it) }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        return readSuccess(response, notFoundIsNoBackup = false)
    }

    private suspend inline fun <reified T> getBodyOrThrow(
        block: suspend () -> HttpResponse,
        notFoundIsNoBackup: Boolean
    ): T = readSuccess(block(), notFoundIsNoBackup)

    private suspend inline fun <reified T> readSuccess(
        response: HttpResponse,
        notFoundIsNoBackup: Boolean
    ): T {
        checkSuccess(response, notFoundIsNoBackup)
        return try {
            response.body()
        } catch (exception: SerializationException) {
            throw SyncError.Unknown(exception)
        }
    }

    private suspend fun checkSuccess(response: HttpResponse, notFoundIsNoBackup: Boolean) {
        if (response.status.isSuccess()) return
        throw toSyncError(response, notFoundIsNoBackup)
    }

    private suspend fun toSyncError(response: HttpResponse, notFoundIsNoBackup: Boolean): SyncError {
        val status = response.status.value
        val body = runCatching { response.bodyAsText() }.getOrNull().orEmpty()
        val error = runCatching { ServerSyncJson.decodeFromString<ServerErrorResponse>(body) }.getOrNull()
        return when {
            status == HttpStatusCode.Unauthorized.value -> SyncError.ServerUnauthorized()
            status == HttpStatusCode.NotFound.value && notFoundIsNoBackup -> SyncError.NoBackupFound()
            status == HttpStatusCode.UnprocessableEntity.value &&
                error?.code == CODE_SCHEMA_VERSION_UNSUPPORTED -> SyncError.CorruptedBackupFile()
            status == HttpStatusCode.UnprocessableEntity.value ->
                SyncError.ValidationRejected(error?.message ?: "validation failed")
            else -> SyncError.Unknown(
                IllegalStateException("HTTP $status: ${error?.code ?: "no code"} ${error?.message.orEmpty()}")
            )
        }
    }

    private fun url(baseUrl: String, path: String): String = "${baseUrl.trimEnd('/')}/$path"

    private fun HttpRequestBuilder.authorize(token: String) {
        headers.append("Authorization", "Bearer $token")
    }

    companion object {
        const val PATH_REGISTER = "v1/auth/register"
        const val PATH_ME = "v1/auth/me"
        const val PATH_PUSH = "v1/sync/push"
        const val PATH_PULL = "v1/sync/pull"
        const val PATH_BACKUP = "v1/backup"
        const val CODE_SCHEMA_VERSION_UNSUPPORTED = "schema_version_unsupported"
    }
}

/**
 * Maps transport-level failures (connection refused, DNS, timeouts) into
 * [SyncError.ServerUnreachable]; anything else becomes [SyncError.Unknown].
 */
internal fun mapTransportError(exception: Exception): SyncError = when (exception) {
    is IOException -> SyncError.ServerUnreachable(exception)
    else -> SyncError.Unknown(exception)
}
