package wottrich.github.io.smartchecklist.serversync.model

/** Outcome of pairing against the configured server (`/health` + `/v1/auth/me`). */
sealed class ServerConnectionState {

    /** No server URL/token stored yet. */
    data object NotConfigured : ServerConnectionState()

    /** Token rejected — settings screen should prompt re-pairing. */
    data object Unauthorized : ServerConnectionState()

    /** The server did not answer (offline, wrong host/port, refused). */
    data object Unreachable : ServerConnectionState()

    /** Token accepted. */
    data class Connected(val userId: String) : ServerConnectionState()
}

/**
 * Snapshot shown on the settings screen's sync status row.
 *
 * @param lastSyncAt epoch millis of the last successful sync, null = never.
 * @param pendingChanges outbox entries still waiting to be pushed.
 * @param lastErrorKind machine-readable kind of the last failed sync attempt
 * (null = last attempt succeeded or no attempt was made).
 */
data class ServerSyncStatusModel(
    val isConfigured: Boolean,
    val serverUrl: String?,
    val lastSyncAt: Long?,
    val pendingChanges: Int,
    val lastErrorKind: String?,
)
