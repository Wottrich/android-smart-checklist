package wottrich.github.io.smartchecklist.serversync.data.repository

import kotlinx.coroutines.withContext
import kotlinx.serialization.json.decodeFromJsonElement
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxEntry
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.serversync.api.ChecklistSyncDto
import wottrich.github.io.smartchecklist.serversync.api.ServerApi
import wottrich.github.io.smartchecklist.serversync.api.ServerSyncJson
import wottrich.github.io.smartchecklist.serversync.api.ServerSyncRepository
import wottrich.github.io.smartchecklist.serversync.api.mapTransportError
import wottrich.github.io.smartchecklist.serversync.api.SyncChangeDto
import wottrich.github.io.smartchecklist.serversync.api.SyncChangeEntryDto
import wottrich.github.io.smartchecklist.serversync.api.SyncPushBody
import wottrich.github.io.smartchecklist.serversync.api.TaskSyncDto
import wottrich.github.io.smartchecklist.serversync.data.ServerSettingsDatasource
import wottrich.github.io.smartchecklist.serversync.domain.SyncError
import wottrich.github.io.smartchecklist.serversync.model.ServerConnectionState
import wottrich.github.io.smartchecklist.serversync.model.ServerSyncStatusModel

/**
 * The sync engine: drain the outbox (push, oldest first), then pull server changes
 * until the cursor is caught up. Offline-first invariants:
 *  - no-op success when the server is not configured;
 *  - every failure is swallowed into state (lastSyncAt/lastErrorKind) and returned
 *    as a [Result] failure — never a user-facing error;
 *  - applies go through [SyncDatasource] directly (never the outbox enqueue path),
 *    so sync cannot feed itself.
 */
internal class ServerSyncRepositoryImpl(
    private val dispatchersProviders: DispatchersProviders,
    private val serverSettingsDatasource: ServerSettingsDatasource,
    private val syncOutboxDatasource: SyncOutboxDatasource,
    private val syncDatasource: SyncDatasource,
    private val serverApi: ServerApi,
) : ServerSyncRepository {

    override suspend fun syncNow(): Result<UseCase.Empty> {
        return withContext(dispatchersProviders.io) {
            if (!serverSettingsDatasource.isConfigured()) {
                return@withContext Result.success(UseCase.Empty())
            }
            val baseUrl = serverSettingsDatasource.getServerUrl().orEmpty()
            val token = serverSettingsDatasource.getToken().orEmpty()
            try {
                pushOutbox(baseUrl, token)
                pullServerChanges(baseUrl, token)
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
                pendingChanges = syncOutboxDatasource.count(),
                lastErrorKind = serverSettingsDatasource.getLastErrorKind(),
            )
        }
    }

    // ---------- push ----------

    /**
     * Drains the outbox oldest-first. `applied | ignored | conflict` all mean the
     * server has the truth now — the entry is dropped. The whole batch is atomic
     * server-side, so a `422` keeps it queued (status row surfaces the error kind).
     */
    private suspend fun pushOutbox(baseUrl: String, token: String) {
        while (true) {
            val batch = syncOutboxDatasource.pending(PUSH_BATCH_SIZE)
            if (batch.isEmpty()) return
            val changes = batch.map { entry ->
                SyncChangeDto(
                    entity = entry.entity,
                    uuid = entry.entityUuid,
                    op = entry.op,
                    at = entry.at,
                    payload = ServerSyncJson.parseToJsonElement(entry.payload),
                )
            }
            val response = serverApi.push(baseUrl, token, SyncPushBody(changes))
            val ackedUuids = response.results
                .filter { it.status in TERMINAL_ACK_STATUSES }
                .map { it.uuid }
                .toSet()
            val ackedIds = batch.filter { it.entityUuid in ackedUuids }.map { it.id }
            syncOutboxDatasource.deleteByIds(ackedIds)
            // nothing acked (or nothing left queued) — stop instead of re-sending forever
            if (ackedIds.isEmpty() || ackedIds.size < batch.size) return
            if (batch.size < PUSH_BATCH_SIZE) return
        }
    }

    // ---------- pull ----------

    /** Pages until `hasMore=false`; the cursor is persisted after each applied page. */
    private suspend fun pullServerChanges(baseUrl: String, token: String) {
        while (true) {
            val cursor = serverSettingsDatasource.getSyncCursor()
            val page = serverApi.pull(baseUrl, token, since = cursor, limit = PULL_PAGE_SIZE)
            applyPullPage(page.changes)
            serverSettingsDatasource.setSyncCursor(page.cursor)
            if (!page.hasMore) return
        }
    }

    /**
     * One Room transaction per page. Server change-log order is chronological and
     * parents-first; within a page upserts go before deletes, which matches LWW
     * for every order the log can produce. Apply is idempotent — a crash between
     * apply and cursor persist simply replays the page.
     */
    private suspend fun applyPullPage(changes: List<SyncChangeEntryDto>) {
        if (changes.isEmpty()) return
        val upsertChecklists = mutableListOf<Checklist>()
        val upsertTasks = mutableListOf<Task>()
        val deleteChecklistUuids = mutableListOf<String>()
        val deleteTaskUuids = mutableListOf<String>()
        for (change in changes) {
            when (change.op) {
                SyncOutboxEntry.OP_UPSERT -> when (change.entity) {
                    SyncOutboxEntry.ENTITY_CHECKLIST ->
                        upsertChecklists += decodeChecklistPayload(change).toModel()
                    SyncOutboxEntry.ENTITY_TASK ->
                        upsertTasks += decodeTaskPayload(change).toModel()
                }
                SyncOutboxEntry.OP_DELETE -> when (change.entity) {
                    SyncOutboxEntry.ENTITY_CHECKLIST -> deleteChecklistUuids += change.uuid
                    SyncOutboxEntry.ENTITY_TASK -> deleteTaskUuids += change.uuid
                }
            }
        }
        syncDatasource.applyPage(
            upsertChecklists = upsertChecklists,
            upsertTasks = upsertTasks,
            deleteChecklistUuids = deleteChecklistUuids,
            deleteTaskUuids = deleteTaskUuids,
        )
    }

    private fun decodeChecklistPayload(change: SyncChangeEntryDto): ChecklistSyncDto =
        ServerSyncJson.decodeFromJsonElement<ChecklistSyncDto>(change.payload)

    private fun decodeTaskPayload(change: SyncChangeEntryDto): TaskSyncDto =
        ServerSyncJson.decodeFromJsonElement<TaskSyncDto>(change.payload)

    private fun ChecklistSyncDto.toModel() = Checklist(
        uuid = uuid,
        parentUuid = parentUuid,
        name = name,
        isSelected = isSelected,
        createdDate = createdDate,
        lastUpdate = lastUpdate,
    )

    private fun TaskSyncDto.toModel() = Task(
        uuid = uuid,
        parentUuid = parentUuid,
        name = name,
        isCompleted = isCompleted,
        dateCreated = dateCreated,
    )

    private companion object {
        const val PUSH_BATCH_SIZE = 100
        const val PULL_PAGE_SIZE = 500

        /** All three terminal acks mean "server has the truth" — the entry is dropped. */
        val TERMINAL_ACK_STATUSES = setOf("applied", "ignored", "conflict")
    }
}
