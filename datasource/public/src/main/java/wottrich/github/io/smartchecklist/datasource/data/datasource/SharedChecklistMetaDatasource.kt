package wottrich.github.io.smartchecklist.datasource.data.datasource

/**
 * Per-checklist share state (main Room, `shared_checklist_meta`): who hosts a shared
 * checklist and where its pull/push stands. One row per shared root checklist —
 * owner rows (`isOwner = true`, loopback) and guest rows alike. This is also the
 * outbox routing table: an entity whose root checklist has an active row here is
 * pushed to that host instead of the PC backend.
 */
data class SharedChecklistMeta(
    /** Root checklist uuid. */
    val checklistUuid: String,
    /** Stable id of the owner device (the share host). */
    val hostDeviceId: String,
    /** Last known host address, `"ip:port"`. */
    val hostAddress: String,
    val shareId: String,
    /** This device's bearer token on that host (guest credential; owner = loopback token). */
    val hostToken: String,
    /** Pull cursor into the host's change log for this share. */
    val cursor: Long = 0,
    /** True = this device hosts the share (loopback). */
    val isOwner: Boolean = false,
    /** True = share ended; local frozen copy, never syncs again. */
    val detached: Boolean = false,
    val lastSyncAt: Long? = null,
)

interface SharedChecklistMetaDatasource {

    suspend fun getByChecklistUuid(checklistUuid: String): SharedChecklistMeta?

    suspend fun getAll(): List<SharedChecklistMeta>

    /** Host ids with at least one active (non-detached) share. */
    suspend fun getDistinctActiveHostIds(): List<String>

    suspend fun upsert(meta: SharedChecklistMeta)

    suspend fun setCursor(checklistUuid: String, cursor: Long)

    suspend fun setDetached(checklistUuid: String, detached: Boolean)

    suspend fun setLastSyncAt(checklistUuid: String, at: Long?)

    suspend fun deleteByChecklistUuid(checklistUuid: String)
}
