package wottrich.github.io.smartchecklist.serversync.data

import kotlinx.serialization.encodeToString
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxEntry
import wottrich.github.io.smartchecklist.serversync.api.ChecklistSyncDto
import wottrich.github.io.smartchecklist.serversync.api.ServerSyncJson
import wottrich.github.io.smartchecklist.serversync.api.SyncOutbox
import wottrich.github.io.smartchecklist.serversync.api.SyncScheduler
import wottrich.github.io.smartchecklist.serversync.api.TaskSyncDto

/**
 * Reads the final row state right after the Room write, freezes it as the payload
 * and queues the entry. Entries are only enqueued for **user** actions — sync-applied
 * writes call the datasources directly — so mutation→sync→mutation loops are impossible.
 * Every hook is a no-op when the server is not configured; enqueues fire the
 * deduplicated expedited one-shot sync.
 */
internal class SyncOutboxImpl(
    private val serverSettingsDatasource: ServerSettingsDatasource,
    private val syncDatasource: SyncDatasource,
    private val syncOutboxDatasource: SyncOutboxDatasource,
    private val syncScheduler: SyncScheduler,
) : SyncOutbox {

    override suspend fun onChecklistUpserted(uuid: String) {
        val row = syncDatasource.getChecklistRow(uuid) ?: return
        enqueue(
            entity = SyncOutboxEntry.ENTITY_CHECKLIST,
            entityUuid = uuid,
            op = SyncOutboxEntry.OP_UPSERT,
            payload = ServerSyncJson.encodeToString(
                ChecklistSyncDto(
                    uuid = row.uuid,
                    name = row.name,
                    parentUuid = row.parentUuid,
                    isSelected = row.isSelected,
                    createdDate = row.createdDate,
                    lastUpdate = row.lastUpdate,
                )
            ),
        )
    }

    override suspend fun onChecklistDeleted(uuid: String) {
        // Only the root is enqueued — the server cascades tombstones to sections
        // and tasks, and the pull delivers them as deletes (contract §push).
        enqueueDelete(SyncOutboxEntry.ENTITY_CHECKLIST, uuid)
    }

    override suspend fun onTaskUpserted(uuid: String) {
        val row = syncDatasource.getTaskRow(uuid) ?: return
        enqueue(
            entity = SyncOutboxEntry.ENTITY_TASK,
            entityUuid = uuid,
            op = SyncOutboxEntry.OP_UPSERT,
            payload = ServerSyncJson.encodeToString(
                TaskSyncDto(
                    uuid = row.uuid,
                    parentUuid = row.parentUuid,
                    name = row.name,
                    isCompleted = row.isCompleted,
                    dateCreated = row.dateCreated,
                )
            ),
        )
    }

    override suspend fun onTaskDeleted(uuid: String) {
        enqueueDelete(SyncOutboxEntry.ENTITY_TASK, uuid)
    }

    private suspend fun enqueueDelete(entity: String, uuid: String) {
        enqueue(
            entity = entity,
            entityUuid = uuid,
            op = SyncOutboxEntry.OP_DELETE,
            payload = """{"uuid": "$uuid"}""",
        )
    }

    private suspend fun enqueue(entity: String, entityUuid: String, op: String, payload: String) {
        if (!serverSettingsDatasource.isConfigured()) return
        syncOutboxDatasource.enqueue(
            SyncOutboxEntry(
                entity = entity,
                entityUuid = entityUuid,
                op = op,
                at = System.currentTimeMillis(),
                payload = payload,
            )
        )
        syncScheduler.scheduleOneShotSync()
    }
}
