package wottrich.github.io.smartchecklist.backup.data.repository

import kotlinx.coroutines.withContext
import wottrich.github.io.smartchecklist.backup.data.BackupPreferencesDatasource
import wottrich.github.io.smartchecklist.backup.data.backupfile.BackupFileModel
import wottrich.github.io.smartchecklist.backup.data.mapper.toBackupModel
import wottrich.github.io.smartchecklist.backup.data.mapper.toChecklistsWithTasks
import wottrich.github.io.smartchecklist.backup.data.serializer.BackupFileSerializer
import wottrich.github.io.smartchecklist.backup.data.serializer.BackupFileSerializerException
import wottrich.github.io.smartchecklist.backup.domain.BackupError
import wottrich.github.io.smartchecklist.backup.google.DriveBackupDatasource
import wottrich.github.io.smartchecklist.backup.google.GoogleDriveAuthorization
import wottrich.github.io.smartchecklist.backup.google.GoogleDriveBackupConfig
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import wottrich.github.io.smartchecklist.datasource.data.datasource.ChecklistDatasource

/**
 * Orchestrates the Google Drive backup: authorization -> Room fetch (or restore)
 * -> serialize/deserialize -> Drive `appDataFolder` write/read.
 *
 * Consent seam: `BackupError.NeedsConsent` carries no Android type on purpose —
 * the ViewModel resolves the interactive consent through the impl-level
 * [GoogleDriveAuthorization] and relaunches the action once consent is granted.
 *
 * Authorization runs before every Drive operation: tokens expire (~1h) and are
 * never persisted (this app has no secret storage by design).
 */
internal class BackupRepositoryImpl(
    private val dispatchersProviders: DispatchersProviders,
    private val checklistDatasource: ChecklistDatasource,
    private val driveBackupDatasource: DriveBackupDatasource,
    private val googleDriveAuthorization: GoogleDriveAuthorization,
    private val backupPreferencesDatasource: BackupPreferencesDatasource,
    private val backupFileSerializer: BackupFileSerializer,
) : BackupRepository {

    override suspend fun getBackupStatus(): BackupStatusModel = BackupStatusModel(
        isConnected = backupPreferencesDatasource.isConnected(),
        connectedAccountEmail = backupPreferencesDatasource.getConnectedAccountEmail(),
        lastBackupDate = backupPreferencesDatasource.getLastBackupDate()
    )

    override suspend fun connect(): Result<BackupStatusModel> {
        val tokenResult = accessTokenOrFailure()
        val granted = tokenResult.getOrNull()
            ?: return Result.failure(checkNotNull(tokenResult.exceptionOrNull()))

        // The email is best-effort: the authorization result does not always carry
        // the account, but the consent itself is what "connected" means.
        val status = withContext(dispatchersProviders.io) {
            backupPreferencesDatasource.setConnectionState(
                connected = true,
                connectedAccountEmail = granted.accountEmail,
                lastBackupDate = backupPreferencesDatasource.getLastBackupDate()
            )
            getBackupStatus()
        }
        return Result.success(status)
    }

    override suspend fun disconnect(): Result<UseCase.Empty> {
        return withContext(dispatchersProviders.io) {
            googleDriveAuthorization.revokeAccess()
            backupPreferencesDatasource.setConnectionState(
                connected = false,
                connectedAccountEmail = null,
                lastBackupDate = null
            )
            Result.success(UseCase.Empty())
        }
    }

    override suspend fun createBackup(): Result<UseCase.Empty> {
        val tokenResult = accessTokenOrFailure()
        val granted = tokenResult.getOrNull()
            ?: return Result.failure(checkNotNull(tokenResult.exceptionOrNull()))

        return withContext(dispatchersProviders.io) {
            val now = System.currentTimeMillis()
            val backupFile = BackupFileModel(
                createdAt = now,
                checklists = checklistDatasource.getAllChecklistsWithTasks().map { it.toBackupModel() }
            )
            val content = try {
                backupFileSerializer.serialize(backupFile).toByteArray(Charsets.UTF_8)
            } catch (exception: Exception) {
                return@withContext Result.failure(BackupError.DriveIoError(exception))
            }
            val writeResult = driveBackupDatasource.write(
                accessToken = granted.accessToken,
                fileName = GoogleDriveBackupConfig.BACKUP_FILE_NAME,
                content = content
            )
            if (writeResult.isFailure) {
                return@withContext Result.failure(
                    toDriveError(checkNotNull(writeResult.exceptionOrNull()))
                )
            }
            backupPreferencesDatasource.setLastBackupDate(now)
            Result.success(UseCase.Empty())
        }
    }

    override suspend fun restoreBackup(): Result<UseCase.Empty> {
        val tokenResult = accessTokenOrFailure()
        val granted = tokenResult.getOrNull()
            ?: return Result.failure(checkNotNull(tokenResult.exceptionOrNull()))

        return withContext(dispatchersProviders.io) {
            val contentResult = driveBackupDatasource.read(
                accessToken = granted.accessToken,
                fileName = GoogleDriveBackupConfig.BACKUP_FILE_NAME
            )
            if (contentResult.isFailure) {
                return@withContext Result.failure(
                    toDriveError(checkNotNull(contentResult.exceptionOrNull()))
                )
            }
            val content: ByteArray? = contentResult.getOrNull()
            if (content == null) {
                return@withContext Result.failure(BackupError.NoBackupFound())
            }

            val backupFile = try {
                backupFileSerializer.deserialize(content.toString(Charsets.UTF_8))
            } catch (expected: BackupFileSerializerException) {
                return@withContext Result.failure(BackupError.CorruptedBackupFile())
            } catch (exception: Exception) {
                return@withContext Result.failure(BackupError.DriveIoError(exception))
            }

            checklistDatasource.replaceAllChecklists(backupFile.toChecklistsWithTasks())
            Result.success(UseCase.Empty())
        }
    }

    /**
     * Silent-first authorization shared by every Drive operation. `Ok` carries the
     * whole [GoogleDriveAuthorization.AuthorizationOutcome.Granted] (token + best
     * effort email); `Err` carries the user-presentable [BackupError].
     */
    private suspend fun accessTokenOrFailure(): Result<GoogleDriveAuthorization.AuthorizationOutcome.Granted> {
        return when (val outcome = googleDriveAuthorization.authorize()) {
            is GoogleDriveAuthorization.AuthorizationOutcome.Granted -> Result.success(outcome)
            is GoogleDriveAuthorization.AuthorizationOutcome.NeedsConsent ->
                Result.failure(BackupError.NeedsConsent())

            is GoogleDriveAuthorization.AuthorizationOutcome.Failed ->
                Result.failure(BackupError.DriveIoError(outcome.exception))
        }
    }

    private fun toDriveError(exception: Throwable): Exception = when (exception) {
        is BackupError -> exception
        else -> BackupError.DriveIoError(exception as? Exception ?: Exception(exception))
    }
}
