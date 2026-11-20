package wottrich.github.io.smartchecklist.serversync.api

/**
 * Mutation→outbox seam. Feature repositories call these right after a **user** action
 * lands in Room; the implementation reads the final row, freezes its payload and
 * queues the entry — routed to the right host (`shared_checklist_meta` walk:
 * entity → parent → root checklist; `null` host = PC backend). Sync-applied writes
 * never go through here (they call the datasources directly), which is what makes
 * mutation→sync→mutation loops impossible.
 *
 * Delete hooks carry the entity's `parentUuid` because the row is already gone when
 * they fire — the routing walk needs it to find the root checklist.
 *
 * No-op when no sync target is configured.
 */
interface SyncOutbox {

    suspend fun onChecklistUpserted(uuid: String)

    /** @param parentUuid the deleted checklist's parent (null = it was a root). */
    suspend fun onChecklistDeleted(uuid: String, parentUuid: String?)

    suspend fun onTaskUpserted(uuid: String)

    /** @param parentUuid the deleted task's parent checklist uuid. */
    suspend fun onTaskDeleted(uuid: String, parentUuid: String?)

    /**
     * Selection is **device-local**: never enqueued for shared checklists (two guests
     * selecting different checklists must not fight via LWW). Personal checklists
     * enqueue a regular upsert.
     */
    suspend fun onChecklistSelectionChanged(uuid: String)
}
