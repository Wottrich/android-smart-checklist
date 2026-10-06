package wottrich.github.io.smartchecklist.backup.domain.usecase

import wottrich.github.io.smartchecklist.backup.data.repository.BackupRepository
import wottrich.github.io.smartchecklist.backup.data.repository.BackupStatusModel
import wottrich.github.io.smartchecklist.backup.domain.ConnectGoogleDriveUseCase
import wottrich.github.io.smartchecklist.backup.domain.CreateBackupUseCase
import wottrich.github.io.smartchecklist.backup.domain.DisconnectGoogleDriveUseCase
import wottrich.github.io.smartchecklist.backup.domain.GetBackupStatusUseCase
import wottrich.github.io.smartchecklist.backup.domain.RestoreBackupUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result

internal class GetBackupStatusUseCaseImpl(
    private val backupRepository: BackupRepository
) : GetBackupStatusUseCase() {
    override suspend fun execute(params: UseCase.None): Result<BackupStatusModel> {
        return Result.success(backupRepository.getBackupStatus())
    }
}

internal class ConnectGoogleDriveUseCaseImpl(
    private val backupRepository: BackupRepository
) : ConnectGoogleDriveUseCase() {
    override suspend fun execute(params: UseCase.None): Result<BackupStatusModel> {
        return backupRepository.connect()
    }
}

internal class DisconnectGoogleDriveUseCaseImpl(
    private val backupRepository: BackupRepository
) : DisconnectGoogleDriveUseCase() {
    override suspend fun execute(params: UseCase.None): Result<UseCase.Empty> {
        return backupRepository.disconnect()
    }
}

internal class CreateBackupUseCaseImpl(
    private val backupRepository: BackupRepository
) : CreateBackupUseCase() {
    override suspend fun execute(params: UseCase.None): Result<UseCase.Empty> {
        return backupRepository.createBackup()
    }
}

internal class RestoreBackupUseCaseImpl(
    private val backupRepository: BackupRepository
) : RestoreBackupUseCase() {
    override suspend fun execute(params: UseCase.None): Result<UseCase.Empty> {
        return backupRepository.restoreBackup()
    }
}
