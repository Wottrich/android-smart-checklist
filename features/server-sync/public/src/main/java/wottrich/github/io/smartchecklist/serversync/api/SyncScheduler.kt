package wottrich.github.io.smartchecklist.serversync.api

/**
 * WorkManager scheduling surface. The periodic sync is a safety net (15 min,
 * UNMETERED); every mutation also fires a deduplicated expedited one-shot.
 * All calls are no-ops-ish when the server is not configured — the worker itself
 * exits early, so scheduling is always safe.
 */
interface SyncScheduler {

    /** Safety net: repeating sync every 15 min on unmetered networks (unique work). */
    fun schedulePeriodicSync()

    /** Best-effort immediate sync after a local mutation (unique expedited work). */
    fun scheduleOneShotSync()
}
