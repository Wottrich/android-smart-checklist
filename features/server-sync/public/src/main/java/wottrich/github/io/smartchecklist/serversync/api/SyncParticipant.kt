package wottrich.github.io.smartchecklist.serversync.api

/**
 * Extension seam around the one-shot sync: features with their own sync targets
 * register a participant in Koin and the [wottrich.github.io.smartchecklist.serversync.work.SyncWorker]
 * collects every registered participant (`getAll`) after the primary server sync.
 * Failures are swallowed into state by each participant — never user-facing —
 * matching the offline-first engine invariants.
 */
interface SyncParticipant {
    suspend fun onSyncRequested()
}
