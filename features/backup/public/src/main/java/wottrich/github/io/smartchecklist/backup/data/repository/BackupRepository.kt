package wottrich.github.io.smartchecklist.backup.data.repository

import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result

/**
 * Contract for the Google Drive backup feature (issue #94).
 *
 * Implementations must keep all Google Drive / Android specifics internal — this
 * module is pure Kotlin and never exposes SDK types.
 *
 * Interactive consent: `connect()` (and any Drive operation that requires a fresh
 * authorization) may fail with `BackupError.NeedsConsent`. The pending-consent
 * intent is resolved by the implementation layer (screen <-> ViewModel), never
 * through this contract, so it stays free of Android types.
 */
interface BackupRepository {

    suspend fun getBackupStatus(): BackupStatusModel

    suspend fun connect(): Result<BackupStatusModel>

    suspend fun disconnect(): Result<UseCase.Empty>

    suspend fun createBackup(): Result<UseCase.Empty>

    suspend fun restoreBackup(): Result<UseCase.Empty>
}

/**
 * @param isConnected whether the `drive.appdata` consent is currently granted.
 * Kept independent from [connectedAccountEmail]: the authorization result does
 * not always expose the account email.
 * @param connectedAccountEmail best-effort display info, null when unknown.
 * @param lastBackupDate epoch millis, null means "never backed up".
 */
data class BackupStatusModel(
    val isConnected: Boolean,
    val connectedAccountEmail: String?,
    val lastBackupDate: Long?,
)
