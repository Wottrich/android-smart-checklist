package wottrich.github.io.smartchecklist.backup.google

import wottrich.github.io.smartchecklist.coroutines.base.Result

/**
 * The only seam allowed to touch the Google Drive API. Scoped to the hidden
 * `appDataFolder` (scope `drive.appdata`) — the user never sees these files in
 * their Drive UI.
 */
interface DriveBackupDatasource {

    /** Creates or updates [fileName] with [content]. */
    suspend fun write(accessToken: String, fileName: String, content: ByteArray): Result<Unit>

    /**
     * Reads [fileName] content, or `null` when no backup exists yet.
     * A missing file is reported as success(null) — only real I/O failures are errors.
     */
    suspend fun read(accessToken: String, fileName: String): Result<ByteArray?>
}
