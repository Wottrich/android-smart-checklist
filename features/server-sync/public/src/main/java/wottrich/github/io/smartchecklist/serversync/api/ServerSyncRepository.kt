package wottrich.github.io.smartchecklist.serversync.api

import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.serversync.model.ServerConnectionState
import wottrich.github.io.smartchecklist.serversync.model.ServerSyncStatusModel

/**
 * Contract of the server-sync engine. Implementations must stay offline-first:
 * every call is best-effort, failures come back as [Result] failures carrying a
 * [wottrich.github.io.smartchecklist.serversync.domain.SyncError], and nothing here
 * ever requires the server for the app to work.
 */
interface ServerSyncRepository {

    /**
     * Drains the outbox (push, oldest first) then pulls server changes until the
     * cursor is caught up. No-op success when the server is not configured.
     */
    suspend fun syncNow(): Result<UseCase.Empty>

    /** `/health` + `/v1/auth/me` probe used by the pairing screen. */
    suspend fun testConnection(): Result<ServerConnectionState>

    /**
     * Registers a new device identity at [serverUrl] (`POST /v1/auth/register`),
     * stores url/token/userId and returns the resulting [ServerConnectionState].
     * The token is shown once by the server — the only place it is ever stored.
     */
    suspend fun register(serverUrl: String): Result<ServerConnectionState>

    /** Clears the pairing; never deletes local data. */
    suspend fun disconnect(): Result<UseCase.Empty>

    /** Current status for the settings screen's sync row. */
    suspend fun getSyncStatus(): ServerSyncStatusModel
}
