package wottrich.github.io.smartchecklist.backup.domain

/**
 * User-presentable failure causes of the backup feature. Lets the ViewModel pick
 * a localized message without knowing any Google Drive detail.
 */
sealed class BackupError(exception: Exception? = null) : Exception(exception) {

    /** The user must grant the `drive.appdata` consent interactively. */
    class NeedsConsent : BackupError()

    /** No backup file exists on Drive yet. */
    class NoBackupFound : BackupError()

    /** Any I/O failure while talking to Google Drive. */
    class DriveIoError(exception: Exception) : BackupError(exception)

    /** The stored backup file exists but cannot be parsed (or its schema is unknown). */
    class CorruptedBackupFile : BackupError()
}
