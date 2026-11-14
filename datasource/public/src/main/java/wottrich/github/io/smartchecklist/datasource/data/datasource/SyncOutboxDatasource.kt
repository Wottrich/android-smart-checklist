package wottrich.github.io.smartchecklist.datasource.data.datasource

/**
 * One queued local mutation waiting to be pushed to the server-sync backend.
 *
 * @param entity `checklist` | `task` (see [Companion.ENTITY_CHECKLIST]/[Companion.ENTITY_TASK]).
 * @param op `upsert` | `delete` (see [Companion.OP_UPSERT]/[Companion.OP_DELETE]).
 * @param at device time of the mutation (not the enqueue time) — drives LWW on the server.
 * @param payload JSON snapshot of the row, frozen at enqueue time.
 */
data class SyncOutboxEntry(
    val id: Long = 0,
    val entity: String,
    val entityUuid: String,
    val op: String,
    val at: Long,
    val payload: String,
) {
    companion object {
        const val ENTITY_CHECKLIST = "checklist"
        const val ENTITY_TASK = "task"
        const val OP_UPSERT = "upsert"
        const val OP_DELETE = "delete"
    }
}

/** Outbox of local mutations. Insert-only until the server acks; never cleared locally otherwise. */
interface SyncOutboxDatasource {

    suspend fun enqueue(entry: SyncOutboxEntry)

    /** Oldest-first batch, [limit] entries at most. */
    suspend fun pending(limit: Int): List<SyncOutboxEntry>

    /** Removes acked entries (by outbox id). */
    suspend fun deleteByIds(ids: List<Long>)

    /**
     * Drops every entry — called only when the whole local database is replaced
     * (backup restore), so stale entries cannot resurrect pre-restore rows server-side.
     */
    suspend fun clearAll()

    suspend fun count(): Int
}
