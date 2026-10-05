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
        connectedAccountEmail = backupPreferencesDatasource.getConnectedAccountEmail(),
        lastBackupDate = backupPreferencesDatasource.getLastBackupDate()
    )

    override suspend fun connect(): Result<BackupStatusModel> {
        return when (val outcome = googleDriveAuthorization.authorize()) {
            is GoogleDriveAuthorization.AuthorizationOutcome.Granted -> {
                outcome.accountEmail?.let { email ->
                    backupPreferencesDatasource.setConnectedAccountEmail(email)
                }
                Result.success(getBackupStatus())
            }

            is GoogleDriveAuthorization.AuthorizationOutcome.NeedsConsent ->
                Result.failure(BackupError.NeedsConsent())

            is GoogleDriveAuthorization.AuthorizationOutcome.Failed ->
                Result.failure(BackupError.DriveIoError(outcome.exception))
        }
    }

    override suspend fun disconnect(): Result<UseCase.Empty> {
        return withContext(dispatchersProviders.io) {
            googleDriveAuthorization.revokeAccess()
            backupPreferencesDatasource.setConnectedAccountEmail(null)
            backupPreferencesDatasource.setLastBackupDate(null)
            Result.success(UseCase.Empty())
        }
    }


    override suspend fun createBackup(): Result<UseCase.Empty> {
        val outcome = googleDriveAuthorization.authorize()
        val accessToken = (outcome as? GoogleDriveAuthorization.AuthorizationOutcome.Granted)?.accessToken
            ?: return Result.failure(toAuthorizationError(outcome))

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
                accessToken = accessToken,
                fileName = GoogleDriveBackupConfig.BACKUP_FILE_NAME,
                content = content
            )
            return@withContext when {
                writeResult.isSuccess -> {
                    backupPreferencesDatasource.setLastBackupDate(now)
                    Result.success(UseCase.Empty())
                }

                else -> Result.failure(toDriveError(writeResult.exceptionOrNull() ?: unknownDriveError()))
            }
        }
    }

    override suspend fun restoreBackup(): Result<UseCase.Empty> {
        val outcome = googleDriveAuthorization.authorize()
        val accessToken = (outcome as? GoogleDriveAuthorization.AuthorizationOutcome.Granted)?.accessToken
            ?: return Result.failure(toAuthorizationError(outcome))

        return withContext(dispatchersProviders.io) {
            val contentResult = driveBackupDatasource.read(
                accessToken = accessToken,
                fileName = GoogleDriveBackupConfig.BACKUP_FILE_NAME
            )
            val content: ByteArray? = contentResult.getOrNull()
            if (content == null) {
                // success(null) = no backup yet; a failure carries its own cause.
                val outcome = contentResult.exceptionOrNull()
                    ?.let { Result.failure<UseCase.Empty>(toDriveError(it)) }
                    ?: Result.failure<UseCase.Empty>(BackupError.NoBackupFound())
                return@withContext outcome
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

    private fun toAuthorizationError(outcome: GoogleDriveAuthorization.AuthorizationOutcome): Exception =
        when (outcome) {
            is GoogleDriveAuthorization.AuthorizationOutcome.NeedsConsent -> BackupError.NeedsConsent()
            is GoogleDriveAuthorization.AuthorizationOutcome.Failed -> BackupError.DriveIoError(outcome.exception)
            is GoogleDriveAuthorization.AuthorizationOutcome.Granted ->
                BackupError.DriveIoError(IllegalStateException("Granted without token"))
        }

    private fun toDriveError(exception: Throwable): Exception = when (exception) {
        is BackupError -> exception
        else -> BackupError.DriveIoError(exception as? Exception ?: Exception(exception))
    }

    private fun unknownDriveError(): Exception = IllegalStateException("Drive operation failed without exception")
}
