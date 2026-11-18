package wottrich.github.io.smartchecklist.datasource.data.datasource

import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.datasource.data.model.Task

/**
 * Row-level read/write surface for the server-sync engine — the only datasource whose
 * models always carry the real row dates (the feature-facing `ChecklistDatasource`/
 * `TaskDatasource` drop them in mapping). Sync applies are idempotent: upserting a
 * missing-parent row is the caller's ordering responsibility (parents first), deleting
 * a missing row is a no-op.
 */
interface SyncDatasource {

    /** Full row (dates included) or null when missing. */
    suspend fun getChecklistRow(uuid: String): Checklist?

    /** Full row (dates included) or null when missing. */
    suspend fun getTaskRow(uuid: String): Task?

    /**
     * Applies one pulled page atomically: upserts first (parents-first within
     * checklists), then deletes. Never uses SQL REPLACE — replacing would cascade-delete
     * children; insert-or-update keeps them.
     */
    suspend fun applyPage(
        upsertChecklists: List<Checklist>,
        upsertTasks: List<Task>,
        deleteChecklistUuids: List<String>,
        deleteTaskUuids: List<String>,
    )

    /** Direct delete by uuid; missing row is a no-op. Cascade removes children. */
    suspend fun deleteChecklistByUuid(uuid: String)

    /** Direct delete by uuid; missing row is a no-op. */
    suspend fun deleteTaskByUuid(uuid: String)
}
