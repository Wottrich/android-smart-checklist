package wottrich.github.io.smartchecklist.serversync.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * The one JSON config for the server-sync feature. `ignoreUnknownKeys` keeps older
 * apps forward-compatible when the server adds fields; `encodeDefaults` keeps
 * payloads explicit (matching the contract examples, e.g. `parentUuid: null`).
 */
val ServerSyncJson: Json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

/**
 * Shared Ktor client (OkHttp engine) for the LAN server. Plain HTTP to a
 * user-configured host is allowed by `network_security_config.xml`; the actual
 * base URL is per-request because the user can re-pair at runtime.
 */
class KtorClientFactory {

    /** Requests always carry absolute URLs (the base URL is user-configured at runtime). */
    fun create(): HttpClient = HttpClient(OkHttp) {
        expectSuccess = false // statuses are mapped to SyncError manually
        install(ContentNegotiation) { json(ServerSyncJson) }
        install(HttpTimeout) {
            // LAN server: fail fast so sync failures stay invisible to the user
            connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
            requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
        }
    }

    private companion object {
        const val CONNECT_TIMEOUT_MILLIS = 5_000L
        const val REQUEST_TIMEOUT_MILLIS = 15_000L
    }
}
