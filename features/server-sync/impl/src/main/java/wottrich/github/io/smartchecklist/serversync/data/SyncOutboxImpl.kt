package wottrich.github.io.smartchecklist.serversync.data

import wottrich.github.io.smartchecklist.datasource.data.datasource.SharedChecklistMeta
import wottrich.github.io.smartchecklist.datasource.data.datasource.SharedChecklistMetaDatasource
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
 *
 * Routing (Phase B of the Wi-Fi sharing plan): each entry is routed by walking the
 * entity → parent → root checklist chain and looking the root up in
 * `shared_checklist_meta`. Personal checklists enqueue with `hostId = null`
 * (PC backend); entities of a shared checklist enqueue with the owner host's id;
 * detached (share-ended) copies enqueue nothing. Selection changes on shared
 * checklists are never enqueued — selection is device-local.
 */
internal class SyncOutboxImpl(
    private val serverSettingsDatasource: ServerSettingsDatasource,
    private val syncDatasource: SyncDatasource,
    private val syncOutboxDatasource: SyncOutboxDatasource,
    private val sharedChecklistMetaDatasource: SharedChecklistMetaDatasource,
    private val syncScheduler: SyncScheduler,
) : SyncOutbox {

    override suspend fun onChecklistUpserted(uuid: String) {
        val row = syncDatasource.getChecklistRow(uuid) ?: return
        val meta = resolveMeta(entity = SyncOutboxEntry.ENTITY_CHECKLIST, uuid = uuid, parentUuidHint = row.parentUuid)
        if (meta?.detached == true) return
        enqueue(
            entity = SyncOutboxEntry.ENTITY_CHECKLIST,
            entityUuid = uuid,
            op = SyncOutboxEntry.OP_UPSERT,
            // shared payloads never carry the device-local selection flag
            payload = ServerSyncJson.encodeToString(
                ChecklistSyncDto(
                    uuid = row.uuid,
                    name = row.name,
                    parentUuid = row.parentUuid,
                    isSelected = row.isSelected && meta == null,
                    createdDate = row.createdDate,
                    lastUpdate = row.lastUpdate,
                )
            ),
            hostId = meta?.hostDeviceId,
        )
    }

    override suspend fun onChecklistDeleted(uuid: String, parentUuid: String?) {
        val meta = resolveMeta(entity = SyncOutboxEntry.ENTITY_CHECKLIST, uuid = uuid, parentUuidHint = parentUuid)
        if (meta?.detached == true) return
        enqueueDelete(SyncOutboxEntry.ENTITY_CHECKLIST, uuid, meta?.hostDeviceId)
    }

    override suspend fun onTaskUpserted(uuid: String) {
        val row = syncDatasource.getTaskRow(uuid) ?: return
        val meta = resolveMeta(entity = SyncOutboxEntry.ENTITY_TASK, uuid = uuid, parentUuidHint = row.parentUuid)
        if (meta?.detached == true) return
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
            hostId = meta?.hostDeviceId,
        )
    }

    override suspend fun onTaskDeleted(uuid: String, parentUuid: String?) {
        val meta = resolveMeta(entity = SyncOutboxEntry.ENTITY_TASK, uuid = uuid, parentUuidHint = parentUuid)
        if (meta?.detached == true) return
        enqueueDelete(SyncOutboxEntry.ENTITY_TASK, uuid, meta?.hostDeviceId)
    }

    override suspend fun onChecklistSelectionChanged(uuid: String) {
        val meta = resolveMeta(entity = SyncOutboxEntry.ENTITY_CHECKLIST, uuid = uuid, parentUuidHint = null)
        // shared (even detached) → selection is device-local, nothing to sync
        if (meta != null) return
        onChecklistUpserted(uuid)
    }

    private suspend fun enqueueDelete(entity: String, uuid: String, hostId: String?) {
        enqueue(
            entity = entity,
            entityUuid = uuid,
            op = SyncOutboxEntry.OP_DELETE,
            payload = """{"uuid": "$uuid"}""",
            hostId = hostId,
        )
    }

    private suspend fun enqueue(entity: String, entityUuid: String, op: String, payload: String, hostId: String?) {
        if (!serverSettingsDatasource.isConfigured() && hostId == null) return
        syncOutboxDatasource.enqueue(
            SyncOutboxEntry(
                entity = entity,
                entityUuid = entityUuid,
                op = op,
                at = System.currentTimeMillis(),
                payload = payload,
                hostId = hostId,
            )
        )
        syncScheduler.scheduleOneShotSync()
    }

    /**
     * Walks the entity up to its root checklist and finds its share meta.
     * `null` = personal (no share on the root) or the chain is unresolvable.
     */
    private suspend fun resolveMeta(entity: String, uuid: String, parentUuidHint: String?): SharedChecklistMeta? {
        val rootUuid = resolveRootUuid(entity, uuid, parentUuidHint) ?: return null
        return sharedChecklistMetaDatasource.getByChecklistUuid(rootUuid)
    }

    private suspend fun resolveRootUuid(entity: String, uuid: String, parentUuidHint: String?): String? {
        // nearest known checklist to start walking up from:
        // tasks hang off their parent checklist; deleted checklists fall back to the hint
        val start = when (entity) {
            SyncOutboxEntry.ENTITY_TASK ->
                syncDatasource.getTaskRow(uuid)?.parentUuid ?: parentUuidHint ?: return null
            else -> {
                val rowParent = syncDatasource.getChecklistRow(uuid)?.parentUuid ?: parentUuidHint
                if (rowParent == null) uuid else rowParent
            }
        }
        var current = start
        val visited = mutableSetOf(current)
        while (true) {
            val parentUuid = syncDatasource.getChecklistRow(current)?.parentUuid ?: return current
            if (!visited.add(parentUuid)) return null // broken chain guard
            current = parentUuid
        }
    }
}
