package wottrich.github.io.smartchecklist.serversync.api

/**
 * Mutation→outbox seam. Feature repositories call these right after a **user** action
 * lands in Room; the implementation reads the final row, freezes its payload and
 * queues the entry. Sync-applied writes never go through here (they call the
 * datasources directly), which is what makes mutation→sync→mutation loops impossible.
 *
 * No-op when the server is not configured.
 */
interface SyncOutbox {

    suspend fun onChecklistUpserted(uuid: String)

    suspend fun onChecklistDeleted(uuid: String)

    suspend fun onTaskUpserted(uuid: String)

    suspend fun onTaskDeleted(uuid: String)
}
