package wottrich.github.io.smartchecklist.share.host

import androidx.room.withTransaction
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxEntry
import wottrich.github.io.smartchecklist.serversync.api.ChecklistSyncDto
import wottrich.github.io.smartchecklist.serversync.api.ServerSyncJson
import wottrich.github.io.smartchecklist.serversync.api.SyncAckResponse
import wottrich.github.io.smartchecklist.serversync.api.SyncChangeDto
import wottrich.github.io.smartchecklist.serversync.api.TaskSyncDto
import wottrich.github.io.smartchecklist.share.domain.ShareError
import wottrich.github.io.smartchecklist.share.host.db.HostChecklistDTO
import wottrich.github.io.smartchecklist.share.host.db.HostTaskDTO
import wottrich.github.io.smartchecklist.share.host.db.ShareChangeLogDTO
import wottrich.github.io.smartchecklist.share.host.db.ShareDao
import wottrich.github.io.smartchecklist.share.host.db.ShareHostDatabase
import wottrich.github.io.smartchecklist.share.host.db.ShareTombstoneDTO

/**
 * Owner-side ingest — a port of the PC backend's `SyncService` rules onto the
 * embedded host database (`share-contract.md`):
 *
 *  - **parents-first ingest**: within a batch, checklist upserts are applied in
 *    parent-chain order, checklist upserts before task upserts, deletes last —
 *    a change referencing a missing parent fails the whole batch with `422`;
 *  - **LWW by client `at`**: a change not strictly newer than the stored state
 *    (mirror row or tombstone) is acked `ignored`, never rejected — which also
 *    makes re-pushing an applied change a no-op (idempotency);
 *  - **deletes are cascading tombstones**: deleting a checklist tombstones its
 *    sections and tasks, each with its own change-log row;
 *  - **share scoping**: every change must resolve into a share the caller is a
 *    member of (existing row's share, or the parent chain's share);
 *  - **`isSelected` never syncs**: share payloads are forced to `false` at ingest;
 *  - **whole-batch atomicity**: everything runs in one `share_host.db` transaction —
 *    a validation failure aborts the entire push.
 */
class ShareHostIngestService(
    private val database: ShareHostDatabase,
    private val dao: ShareDao,
) {

    /**
     * Transaction seam — production runs the batch in one `share_host.db`
     * transaction (whole-batch atomicity); JVM tests replace it with direct
     * execution (Room transactions need an Android runtime).
     */
    internal var transactionRunner: suspend (block: suspend () -> Unit) -> Unit =
        { database.withTransaction(it) }

    /**
     * Applies a push batch for [userId] (already authenticated). Returns per-change
     * acks in the same order as the input, or throws [ShareError] (mapped to `422` /
     * `403` by the server) with the batch left untouched.
     */
    suspend fun ingest(userId: String, changes: List<SyncChangeDto>): List<SyncAckResponse> {
        val memberShareIds = dao.getMemberShareIds(userId).toSet()
        if (memberShareIds.isEmpty() && changes.isNotEmpty()) {
            throw ShareError.ShareNotAccepted()
        }
        val ordered = orderParentsFirst(changes)
        val acks = mutableListOf<SyncAckResponse>()
        val now = System.currentTimeMillis()
        transactionRunner {
            for (change in ordered) {
                // v2 hardening: client `at` drives LWW — a far-future stamp would
                // win every conflict forever, so the whole batch is rejected
                if (change.at > now + CLOCK_SKEW_MS) {
                    throw ShareError.ValidationRejected("clock skew: at is in the future")
                }
                val (shareId, payload) = resolveScope(change, memberShareIds)
                val changeWithSharePayload = change.copy(payload = payload)
                val applied = applyChange(userId, changeWithSharePayload, shareId)
                acks += SyncAckResponse(uuid = change.uuid, status = if (applied) STATUS_APPLIED else STATUS_IGNORED)
            }
        }
        // keep the response in the caller's original order
        val byUuid = acks.associateBy { it.uuid }
        return changes.map { byUuid.getValue(it.uuid) }
    }

    /**
     * Checklist upserts first (parents before children), then task upserts, then
     * deletes — the order the change log must produce for pulls to stay valid.
     */
    private fun orderParentsFirst(changes: List<SyncChangeDto>): List<SyncChangeDto> {
        val checklistUpserts = changes
            .filter { it.entity == SyncOutboxEntry.ENTITY_CHECKLIST && it.op == SyncOutboxEntry.OP_UPSERT }
            .sortedBy { change ->
                // depth of the parent chain within the batch payload; roots (null) first
                var depth = 0
                var parent: String? = decodeChecklist(change).parentUuid
                val seen = mutableSetOf<String>()
                while (parent != null && seen.add(parent)) {
                    depth++
                    parent = changes
                        .filter { it.uuid == parent && it.entity == SyncOutboxEntry.ENTITY_CHECKLIST }
                        .firstOrNull()
                        ?.let { decodeChecklist(it).parentUuid }
                }
                depth
            }
        val taskUpserts = changes.filter { it.entity == SyncOutboxEntry.ENTITY_TASK && it.op == SyncOutboxEntry.OP_UPSERT }
        val deletes = changes.filter { it.op == SyncOutboxEntry.OP_DELETE }
        val rest = changes.filterNot { it in checklistUpserts || it in taskUpserts || it in deletes }
        return checklistUpserts + taskUpserts + deletes + rest
    }

    /**
     * Finds the share a change belongs to and its normalized payload. An unknown
     * entity/op, a payload outside any accepted share, or a member-less root
     * creation → [ShareError]. Payload is re-serialized without the selection flag.
     */
    private suspend fun resolveScope(change: SyncChangeDto, memberShareIds: Set<String>): Pair<String, JsonObject> {
        when (change.entity) {
            SyncOutboxEntry.ENTITY_CHECKLIST -> when (change.op) {
                SyncOutboxEntry.OP_UPSERT -> {
                    val dto = decodeChecklist(change)
                    val shareId = dao.findChecklist(change.uuid)?.shareId
                    if (shareId != null) return scopedShare(shareId, memberShareIds) to normalize(dto)
                    val parentUuid = dto.parentUuid
                    if (parentUuid != null) {
                        // new section: a guest may only add inside an accepted share —
                        // the parent chain determines the share
                        val parentRow = dao.findChecklist(parentUuid)
                            ?: throw ShareError.ValidationRejected("missing parent $parentUuid")
                        return scopedShare(parentRow.shareId, memberShareIds) to normalize(dto)
                    }
                    // root row (new or stale re-push of a deleted root): scope via the
                    // tombstone, or via the share that advertises this root
                    val tombstoneShare = dao.findTombstone(change.uuid)?.shareId
                    val shareByRoot = dao.getActiveShares().firstOrNull { it.rootUuid == change.uuid }?.shareId
                    return scopedShare(
                        tombstoneShare ?: shareByRoot
                            ?: throw ShareError.ValidationRejected("unknown root ${change.uuid}"),
                        memberShareIds,
                    ) to normalize(dto)
                }
                SyncOutboxEntry.OP_DELETE -> {
                    val shareId = dao.findChecklist(change.uuid)?.shareId
                        ?: dao.findTombstone(change.uuid)?.shareId
                        ?: throw ShareError.ValidationRejected("unknown checklist ${change.uuid}")
                    return scopedShare(shareId, memberShareIds) to deletePayload(change.uuid)
                }
                else -> throw ShareError.ValidationRejected("unknown op ${change.op}")
            }
            SyncOutboxEntry.ENTITY_TASK -> when (change.op) {
                SyncOutboxEntry.OP_UPSERT -> {
                    val dto = decodeTask(change)
                    val parentRow = dao.findChecklist(dto.parentUuid)
                        ?: throw ShareError.ValidationRejected("missing parent ${dto.parentUuid}")
                    val shareId = dao.findTask(change.uuid)?.shareId ?: parentRow.shareId
                    return scopedShare(shareId, memberShareIds) to normalize(dto)
                }
                SyncOutboxEntry.OP_DELETE -> {
                    val shareId = dao.findTask(change.uuid)?.shareId
                        ?: dao.findTombstone(change.uuid)?.shareId
                        ?: throw ShareError.ValidationRejected("unknown task ${change.uuid}")
                    return scopedShare(shareId, memberShareIds) to deletePayload(change.uuid)
                }
                else -> throw ShareError.ValidationRejected("unknown op ${change.op}")
            }
            else -> throw ShareError.ValidationRejected("unknown entity ${change.entity}")
        }
    }

    private suspend fun scopedShare(shareId: String, memberShareIds: Set<String>): String {
        val share = dao.getShare(shareId) ?: throw ShareError.ValidationRejected("share $shareId not found")
        if (!share.active) throw ShareError.ShareNotFound()
        if (shareId !in memberShareIds) throw ShareError.ShareNotAccepted()
        return shareId
    }

    /** Applies one scoped change. Returns false for LWW-ignored changes. */
    private suspend fun applyChange(userId: String, change: SyncChangeDto, shareId: String): Boolean {
        val isNewerThanStored = when (change.entity) {
            SyncOutboxEntry.ENTITY_CHECKLIST -> {
                val mirrorAt = dao.findChecklist(change.uuid)?.at
                val tombstoneAt = dao.findTombstone(change.uuid)?.at
                (mirrorAt ?: Long.MIN_VALUE) < change.at && (tombstoneAt ?: Long.MIN_VALUE) < change.at
            }
            else -> {
                val mirrorAt = dao.findTask(change.uuid)?.at
                val tombstoneAt = dao.findTombstone(change.uuid)?.at
                (mirrorAt ?: Long.MIN_VALUE) < change.at && (tombstoneAt ?: Long.MIN_VALUE) < change.at
            }
        }
        if (!isNewerThanStored) return false

        when {
            change.entity == SyncOutboxEntry.ENTITY_CHECKLIST && change.op == SyncOutboxEntry.OP_UPSERT -> {
                val dto = ServerSyncJson.decodeFromString<ChecklistSyncDto>(change.payload.toString())
                dao.applyChange(
                    change = logRow(change, shareId),
                    checklist = HostChecklistDTO(
                        uuid = dto.uuid,
                        parentUuid = dto.parentUuid,
                        name = dto.name,
                        createdDate = dto.createdDate,
                        lastUpdate = dto.lastUpdate,
                        shareId = shareId,
                        at = change.at,
                    ),
                    task = null,
                    tombstone = null,
                    removeChecklistUuid = null,
                    removeTaskUuid = null,
                )
            }
            change.entity == SyncOutboxEntry.ENTITY_TASK && change.op == SyncOutboxEntry.OP_UPSERT -> {
                val dto = ServerSyncJson.decodeFromString<TaskSyncDto>(change.payload.toString())
                dao.applyChange(
                    change = logRow(change, shareId),
                    checklist = null,
                    task = HostTaskDTO(
                        uuid = dto.uuid,
                        parentUuid = dto.parentUuid,
                        name = dto.name,
                        isCompleted = dto.isCompleted,
                        dateCreated = dto.dateCreated,
                        shareId = shareId,
                        at = change.at,
                    ),
                    tombstone = null,
                    removeChecklistUuid = null,
                    removeTaskUuid = null,
                )
            }
            change.entity == SyncOutboxEntry.ENTITY_CHECKLIST && change.op == SyncOutboxEntry.OP_DELETE -> {
                cascadeDeleteChecklist(change, shareId)
            }
            else -> {
                dao.applyChange(
                    change = logRow(change, shareId),
                    checklist = null,
                    task = null,
                    tombstone = ShareTombstoneDTO(uuid = change.uuid, entity = change.entity, shareId = shareId, at = change.at),
                    removeChecklistUuid = null,
                    removeTaskUuid = change.uuid,
                )
            }
        }
        return true
    }

    /** Deletes the checklist row plus every section/task below it, logging each. */
    private suspend fun cascadeDeleteChecklist(change: SyncChangeDto, shareId: String) {
        val doomed = mutableListOf(change.uuid to SyncOutboxEntry.ENTITY_CHECKLIST)
        val removedChecklists = mutableListOf<String>()
        val removedTasks = mutableListOf<String>()
        val queue = ArrayDeque(listOf(change.uuid))
        while (queue.isNotEmpty()) {
            val parent = queue.removeFirst()
            dao.findChecklistChildren(parent).forEach { child ->
                doomed += child.uuid to SyncOutboxEntry.ENTITY_CHECKLIST
                queue += child.uuid
            }
            dao.findTasks(parent).forEach { child ->
                doomed += child.uuid to SyncOutboxEntry.ENTITY_TASK
                removedTasks += child.uuid
            }
        }
        doomed.forEach { (uuid, entity) ->
            dao.insertChange(
                ShareChangeLogDTO(
                    entity = entity,
                    uuid = uuid,
                    op = SyncOutboxEntry.OP_DELETE,
                    at = change.at,
                    shareId = shareId,
                    payload = deletePayload(uuid).toString(),
                )
            )
            dao.insertTombstone(
                ShareTombstoneDTO(uuid = uuid, entity = entity, shareId = shareId, at = change.at)
            )
            if (entity == SyncOutboxEntry.ENTITY_CHECKLIST) removedChecklists += uuid
        }
        dao.deleteChecklists(removedChecklists)
        dao.deleteTasks(removedTasks)
    }

    private fun logRow(change: SyncChangeDto, shareId: String) = ShareChangeLogDTO(
        entity = change.entity,
        uuid = change.uuid,
        op = change.op,
        at = change.at,
        shareId = shareId,
        payload = change.payload.toString(),
    )

    private fun decodeChecklist(change: SyncChangeDto): ChecklistSyncDto =
        ServerSyncJson.decodeFromString<ChecklistSyncDto>(change.payload.toString())

    private fun decodeTask(change: SyncChangeDto): TaskSyncDto =
        ServerSyncJson.decodeFromString<TaskSyncDto>(change.payload.toString())

    /** Selection is device-local — stripped from every share payload at ingest. */
    private fun normalize(dto: ChecklistSyncDto): JsonObject = buildJsonObject {
        put("uuid", dto.uuid)
        put("name", dto.name)
        put("parentUuid", dto.parentUuid)
        put("isSelected", false)
        put("createdDate", dto.createdDate)
        put("lastUpdate", dto.lastUpdate)
    }

    private fun normalize(dto: TaskSyncDto): JsonObject = buildJsonObject {
        put("uuid", dto.uuid)
        put("parentUuid", dto.parentUuid)
        put("name", dto.name)
        put("isCompleted", dto.isCompleted)
        put("dateCreated", dto.dateCreated)
    }

    private fun deletePayload(uuid: String): JsonObject = buildJsonObject { put("uuid", uuid) }

    private companion object {
        const val STATUS_APPLIED = "applied"
        const val STATUS_IGNORED = "ignored"
        const val CLOCK_SKEW_MS = 5 * 60 * 1_000L
    }
}
