package wottrich.github.io.smartchecklist.serversync.data.repository

import kotlinx.coroutines.withContext
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxDatasource
import wottrich.github.io.smartchecklist.serversync.api.HostSyncEngine
import wottrich.github.io.smartchecklist.serversync.api.ServerApi
import wottrich.github.io.smartchecklist.serversync.api.ServerSyncRepository
import wottrich.github.io.smartchecklist.serversync.api.SyncCursorStore
import wottrich.github.io.smartchecklist.serversync.api.mapTransportError
import wottrich.github.io.smartchecklist.serversync.data.ServerSettingsDatasource
import wottrich.github.io.smartchecklist.serversync.domain.SyncError
import wottrich.github.io.smartchecklist.serversync.model.ServerConnectionState
import wottrich.github.io.smartchecklist.serversync.model.ServerSyncStatusModel

/**
 * PC-backend facade over the extracted [HostSyncSession] engine core. Offline-first
 * invariants:
 *  - no-op success when the server is not configured;
 *  - every failure is swallowed into state (lastSyncAt/lastErrorKind) and returned
 *    as a [Result] failure — never a user-facing error;
 *  - personal entries only: this session drains `hostId = null` (share-host entries
 *    belong to their own per-host sessions — see the Wi-Fi sharing plan, Phase B).
 */
internal class ServerSyncRepositoryImpl(
    private val dispatchersProviders: DispatchersProviders,
    private val serverSettingsDatasource: ServerSettingsDatasource,
    private val syncOutboxDatasource: SyncOutboxDatasource,
    private val hostSyncEngine: HostSyncEngine,
    private val serverApi: ServerApi,
) : ServerSyncRepository {

    private val settingsCursorStore = object : SyncCursorStore {
        override suspend fun get(): Long = serverSettingsDatasource.getSyncCursor()
        override suspend fun set(cursor: Long) = serverSettingsDatasource.setSyncCursor(cursor)
    }

    override suspend fun syncNow(): Result<UseCase.Empty> {
        return withContext(dispatchersProviders.io) {
            if (!serverSettingsDatasource.isConfigured()) {
                return@withContext Result.success(UseCase.Empty())
            }
            val baseUrl = serverSettingsDatasource.getServerUrl().orEmpty()
            val token = serverSettingsDatasource.getToken().orEmpty()
            try {
                hostSyncEngine.pushOutbox(hostId = null, baseUrl, token)
                hostSyncEngine.pullChanges(baseUrl, token, shareId = null, cursorStore = settingsCursorStore)
                serverSettingsDatasource.setLastSyncAt(System.currentTimeMillis())
                serverSettingsDatasource.setLastErrorKind(null)
                Result.success(UseCase.Empty())
            } catch (exception: Exception) {
                val error = exception as? SyncError ?: mapTransportError(exception)
                serverSettingsDatasource.setLastErrorKind(error.kind)
                Result.failure(error)
            }
        }
    }

    override suspend fun testConnection(): Result<ServerConnectionState> {
        return withContext(dispatchersProviders.io) {
            val url = serverSettingsDatasource.getServerUrl()
            val token = serverSettingsDatasource.getToken()
            if (url.isNullOrEmpty() || token.isNullOrEmpty()) {
                return@withContext Result.success(ServerConnectionState.NotConfigured)
            }
            try {
                val me = serverApi.me(url, token)
                Result.success(ServerConnectionState.Connected(me.userId))
            } catch (unauthorized: SyncError.ServerUnauthorized) {
                Result.success(ServerConnectionState.Unauthorized)
            } catch (exception: Exception) {
                // any other failure — wrong host, offline, refused — is just "unreachable"
                Result.success(ServerConnectionState.Unreachable)
            }
        }
    }

    override suspend fun register(serverUrl: String): Result<ServerConnectionState> {
        return withContext(dispatchersProviders.io) {
            val normalizedUrl = serverUrl.trim().trimEnd('/')
            if (normalizedUrl.isEmpty()) {
                return@withContext Result.success(ServerConnectionState.Unreachable)
            }
            try {
                val response = serverApi.register(normalizedUrl, deviceName = android.os.Build.MODEL)
                serverSettingsDatasource.setConnection(normalizedUrl, response.token, response.userId)
                // fresh device identity → replay the server's full change log
                serverSettingsDatasource.setSyncCursor(0L)
                serverSettingsDatasource.setLastSyncAt(null)
                serverSettingsDatasource.setLastErrorKind(null)
                Result.success(ServerConnectionState.Connected(response.userId))
            } catch (exception: Exception) {
                val error = exception as? SyncError ?: mapTransportError(exception)
                Result.success(
                    when (error) {
                        is SyncError.ServerUnauthorized -> ServerConnectionState.Unauthorized
                        else -> ServerConnectionState.Unreachable
                    }
                )
            }
        }
    }

    override suspend fun disconnect(): Result<UseCase.Empty> {
        return withContext(dispatchersProviders.io) {
            // pairing only — local data is never deleted; the worker no-ops unconfigured
            serverSettingsDatasource.clearConnection()
            Result.success(UseCase.Empty())
        }
    }

    override suspend fun getSyncStatus(): ServerSyncStatusModel {
        return withContext(dispatchersProviders.io) {
            ServerSyncStatusModel(
                isConfigured = serverSettingsDatasource.isConfigured(),
                serverUrl = serverSettingsDatasource.getServerUrl(),
                lastSyncAt = serverSettingsDatasource.getLastSyncAt(),
                pendingChanges = syncOutboxPendingForThisHost(),
                lastErrorKind = serverSettingsDatasource.getLastErrorKind(),
            )
        }
    }

    /** Status rows report this feature's queue only — not share-host entries. */
    private suspend fun syncOutboxPendingForThisHost(): Int =
        syncOutboxDatasource.count(hostId = null)
}
