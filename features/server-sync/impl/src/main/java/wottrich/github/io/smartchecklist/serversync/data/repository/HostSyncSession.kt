package wottrich.github.io.smartchecklist.serversync.data.repository

import kotlinx.serialization.json.decodeFromJsonElement
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxEntry
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.serversync.api.ChecklistSyncDto
import wottrich.github.io.smartchecklist.serversync.api.HostSyncEngine
import wottrich.github.io.smartchecklist.serversync.api.ServerApi
import wottrich.github.io.smartchecklist.serversync.api.ServerSyncJson
import wottrich.github.io.smartchecklist.serversync.api.SyncChangeDto
import wottrich.github.io.smartchecklist.serversync.api.SyncChangeEntryDto
import wottrich.github.io.smartchecklist.serversync.api.SyncCursorStore
import wottrich.github.io.smartchecklist.serversync.api.SyncPushBody
import wottrich.github.io.smartchecklist.serversync.api.TaskSyncDto

/**
 * The reusable push/pull/apply core of the sync engine, extracted from
 * `ServerSyncRepositoryImpl` (Phase B of the Wi-Fi sharing plan) so the PC-backend
 * repository and per-host share sessions drive **one** implementation:
 *
 *  - push: drain the outbox for one [hostId] oldest-first; `applied | ignored |
 *    conflict` acks all mean the target has the truth — the entry is dropped;
 *  - pull: page until `hasMore=false`, persisting the cursor after each applied page;
 *  - apply: one Room transaction per page, idempotent (a crash between apply and
 *    cursor persist simply replays the page).
 *
 * Instances are cheap and stateless — the session state (cursor) lives in the
 * [SyncCursorStore], the queue in the outbox datasource.
 */
class HostSyncSession(
    private val syncOutboxDatasource: SyncOutboxDatasource,
    private val syncDatasource: SyncDatasource,
    private val serverApi: ServerApi,
) : HostSyncEngine {

    /**
     * Drains [hostId]'s outbox oldest-first to the given host. The whole batch is
     * atomic target-side, so a `422` keeps it queued for the next session.
     */
    override suspend fun pushOutbox(hostId: String?, baseUrl: String, token: String) {
        while (true) {
            val batch = syncOutboxDatasource.pending(hostId, PUSH_BATCH_SIZE)
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

    /** Pages until `hasMore=false`; the cursor is persisted after each applied page. */
    override suspend fun pullChanges(
        baseUrl: String,
        token: String,
        shareId: String?,
        cursorStore: SyncCursorStore,
    ) {
        while (true) {
            val cursor = cursorStore.get()
            val page = serverApi.pull(baseUrl, token, since = cursor, limit = PULL_PAGE_SIZE, shareId = shareId)
            applyChanges(page.changes)
            cursorStore.set(page.cursor)
            if (!page.hasMore) return
        }
    }

    /**
     * One Room transaction per page. Change-log order is chronological and
     * parents-first; within a page upserts go before deletes, which matches LWW
     * for every order the log can produce.
     */
    override suspend fun applyChanges(changes: List<SyncChangeEntryDto>) {
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

        /** All three terminal acks mean "the target has the truth" — the entry is dropped. */
        val TERMINAL_ACK_STATUSES = setOf("applied", "ignored", "conflict")
    }
}
