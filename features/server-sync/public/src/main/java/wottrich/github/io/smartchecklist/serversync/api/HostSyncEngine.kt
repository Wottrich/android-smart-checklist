package wottrich.github.io.smartchecklist.serversync.api

/**
 * Cursor read/write for one sync session. The PC backend keeps its cursor in
 * `ServerSettingsDatasource`; each (host, share) pair keeps its own in
 * `shared_checklist_meta`. Single writer: only the sync engine writes through this.
 */
interface SyncCursorStore {
    suspend fun get(): Long
    suspend fun set(cursor: Long)
}

/**
 * The reusable push/pull/apply core of the sync engine, behind an interface so the
 * PC-backend repository and per-host share sessions (Wi-Fi sharing, Phase B/C) drive
 * **one** implementation:
 *
 *  - push: drain the outbox for one [pushOutbox] host (`null` = PC backend) oldest-first;
 *  - pull: page until `hasMore=false`, persisting the cursor after each applied page.
 *
 * Instances are stateless — session state lives in the cursor store and the outbox.
 */
interface HostSyncEngine {

    suspend fun pushOutbox(hostId: String?, baseUrl: String, token: String)

    suspend fun pullChanges(
        baseUrl: String,
        token: String,
        shareId: String?,
        cursorStore: SyncCursorStore,
    )

    /**
     * Applies one pulled page atomically (guest accept uses this to materialize a
     * share before the meta row exists — its cursor is persisted by the caller).
     */
    suspend fun applyChanges(changes: List<SyncChangeEntryDto>)
}
