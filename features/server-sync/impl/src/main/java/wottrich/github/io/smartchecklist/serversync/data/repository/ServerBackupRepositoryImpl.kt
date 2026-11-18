package wottrich.github.io.smartchecklist.serversync.data.repository

import kotlinx.coroutines.withContext
import wottrich.github.io.smartchecklist.backup.data.backupfile.BackupFileModel
import wottrich.github.io.smartchecklist.backup.data.backupfile.ChecklistBackupModel
import wottrich.github.io.smartchecklist.backup.data.backupfile.TaskBackupModel
import wottrich.github.io.smartchecklist.backup.data.repository.BackupRepository
import wottrich.github.io.smartchecklist.backup.data.repository.BackupStatusModel
import wottrich.github.io.smartchecklist.backup.domain.BackupError
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import wottrich.github.io.smartchecklist.datasource.data.datasource.ChecklistDatasource
import wottrich.github.io.smartchecklist.serversync.api.ServerApi
import wottrich.github.io.smartchecklist.serversync.data.ServerBackupFileSerializer
import wottrich.github.io.smartchecklist.serversync.data.ServerBackupFileSerializerException
import wottrich.github.io.smartchecklist.serversync.data.ServerSettingsDatasource
import wottrich.github.io.smartchecklist.serversync.domain.SyncError

/**
 * `BackupRepository` backed by the self-hosted server (`/v1/backup`) — same
 * contract as the Drive impl (Phase F flag flip), so the Backup screen works
 * unchanged. `connectedAccountEmail` doubles as the display label and carries
 * the server URL in server mode.
 */
internal class ServerBackupRepositoryImpl(
    private val dispatchersProviders: DispatchersProviders,
    private val serverSettingsDatasource: ServerSettingsDatasource,
    private val serverApi: ServerApi,
    private val checklistDatasource: ChecklistDatasource,
    private val backupFileSerializer: ServerBackupFileSerializer,
) : BackupRepository {

    override suspend fun getBackupStatus(): BackupStatusModel {
        return withContext(dispatchersProviders.io) {
            val url = serverSettingsDatasource.getServerUrl()
            val token = serverSettingsDatasource.getToken()
            if (url.isNullOrEmpty() || token.isNullOrEmpty()) {
                return@withContext BackupStatusModel(
                    isConnected = false,
                    connectedAccountEmail = null,
                    lastBackupDate = null,
                )
            }
            val status = try {
                serverApi.backupStatus(url, token)
            } catch (expected: Exception) {
                // status is best-effort; the pairing itself is what "connected" means
                null
            }
            BackupStatusModel(
                isConnected = true,
                connectedAccountEmail = url,
                lastBackupDate = status?.lastBackupDate,
            )
        }
    }

    override suspend fun connect(): Result<BackupStatusModel> {
        return withContext(dispatchersProviders.io) {
            if (!serverSettingsDatasource.isConfigured()) {
                return@withContext Result.failure(BackupError.NeedsConsent())
            }
            Result.success(getBackupStatus())
        }
    }

    override suspend fun disconnect(): Result<UseCase.Empty> {
        return withContext(dispatchersProviders.io) {
            serverSettingsDatasource.clearConnection()
            Result.success(UseCase.Empty())
        }
    }

    override suspend fun createBackup(): Result<UseCase.Empty> {
        return withContext(dispatchersProviders.io) {
            val url = serverSettingsDatasource.getServerUrl()
            val token = serverSettingsDatasource.getToken()
            if (url.isNullOrEmpty() || token.isNullOrEmpty()) {
                return@withContext Result.failure(BackupError.NeedsConsent())
            }
            val backupFile = BackupFileModel(
                createdAt = System.currentTimeMillis(),
                checklists = checklistDatasource.getAllChecklistsWithTasks().map { it.toBackupModel() },
            )
            try {
                serverApi.uploadBackup(url, token, backupFileSerializer.serialize(backupFile))
            } catch (expected: Exception) {
                return@withContext Result.failure(toBackupError(expected))
            }
            Result.success(UseCase.Empty())
        }
    }

    override suspend fun restoreBackup(): Result<UseCase.Empty> {
        return withContext(dispatchersProviders.io) {
            val url = serverSettingsDatasource.getServerUrl()
            val token = serverSettingsDatasource.getToken()
            if (url.isNullOrEmpty() || token.isNullOrEmpty()) {
                return@withContext Result.failure(BackupError.NeedsConsent())
            }
            val content = try {
                serverApi.downloadBackup(url, token)
            } catch (expected: Exception) {
                return@withContext Result.failure(toBackupError(expected))
            }
            val backupFile = try {
                backupFileSerializer.deserialize(content)
            } catch (expected: ServerBackupFileSerializerException) {
                return@withContext Result.failure(BackupError.CorruptedBackupFile())
            } catch (expected: Exception) {
                return@withContext Result.failure(BackupError.DriveIoError(expected))
            }
            // replaceAllChecklists also clears the sync outbox (stale entries must
            // not resurrect pre-restore rows server-side)
            checklistDatasource.replaceAllChecklists(backupFile.toChecklistsWithTasks())
            Result.success(UseCase.Empty())
        }
    }

    private fun toBackupError(exception: Exception): BackupError = when (exception) {
        is SyncError -> when (exception) {
            is SyncError.NoBackupFound -> BackupError.NoBackupFound()
            is SyncError.CorruptedBackupFile -> BackupError.CorruptedBackupFile()
            is SyncError.ServerUnauthorized -> BackupError.DriveIoError(exception)
            else -> BackupError.DriveIoError(exception)
        }
        else -> BackupError.DriveIoError(exception)
    }
}

/** Server-side mirror of the backup impl's `BackupModelsMapper` (that one is internal). */
private fun wottrich.github.io.smartchecklist.datasource.data.model.ChecklistWithTasks.toBackupModel() =
    ChecklistBackupModel(
        uuid = checklist.uuid,
        parentUuid = checklist.parentUuid,
        name = checklist.name,
        isSelected = checklist.isSelected,
        createdDate = checklist.createdDate,
        lastUpdate = checklist.lastUpdate,
        tasks = tasks.map { task ->
            TaskBackupModel(
                uuid = task.uuid,
                parentUuid = task.parentUuid,
                name = task.name,
                isCompleted = task.isCompleted,
                dateCreated = task.dateCreated,
            )
        },
    )

private fun wottrich.github.io.smartchecklist.backup.data.backupfile.BackupFileModel.toChecklistsWithTasks() =
    checklists.map { row ->
        wottrich.github.io.smartchecklist.datasource.data.model.ChecklistWithTasks(
            checklist = wottrich.github.io.smartchecklist.datasource.data.model.Checklist(
                uuid = row.uuid,
                parentUuid = row.parentUuid,
                name = row.name,
                isSelected = row.isSelected,
                createdDate = row.createdDate,
                lastUpdate = row.lastUpdate,
            ),
            tasks = row.tasks.map { task ->
                wottrich.github.io.smartchecklist.datasource.data.model.Task(
                    uuid = task.uuid,
                    parentUuid = task.parentUuid,
                    name = task.name,
                    isCompleted = task.isCompleted,
                    dateCreated = task.dateCreated,
                )
            },
            checklistSectionEmbedded = listOf(),
        )
    }
