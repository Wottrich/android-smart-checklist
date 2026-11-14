package wottrich.github.io.smartchecklist.serversync.domain

/**
 * User-presentable failure causes of the server-sync feature — mirrors
 * [wottrich.github.io.smartchecklist.backup.domain.BackupError] style so ViewModels
 * can pick a localized message without knowing any HTTP detail.
 */
sealed class SyncError(exception: Exception? = null) : Exception(exception) {

    /** Machine-readable kind, persisted for the settings screen's status row. */
    abstract val kind: String

    /** The configured server did not answer (connection refused, timeout, DNS). */
    class ServerUnreachable(exception: Exception) : SyncError(exception) {
        override val kind: String = "server_unreachable"
    }

    /** `401 unauthenticated` — the stored token is invalid; re-pairing is required. */
    class ServerUnauthorized : SyncError() {
        override val kind: String = "server_unauthorized"
    }

    /** `404 not_found` — e.g. no backup stored on the server yet (restore's null-path). */
    class NoBackupFound : SyncError() {
        override val kind: String = "no_backup_found"
    }

    /** `422 schema_version_unsupported` — backup version beyond what this app knows. */
    class CorruptedBackupFile : SyncError() {
        override val kind: String = "corrupted_backup_file"
    }

    /** `422 validation` — the server rejected the pushed batch; the batch stays queued. */
    class ValidationRejected(message: String) : SyncError(IllegalStateException(message)) {
        override val kind: String = "validation_rejected"
    }

    /** Anything else (malformed response, 5xx, unknown codes). */
    class Unknown(exception: Exception) : SyncError(exception) {
        override val kind: String = "unknown"
    }
}
