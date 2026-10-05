package wottrich.github.io.smartchecklist.backup.domain

import wottrich.github.io.smartchecklist.backup.data.repository.BackupStatusModel
import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase

abstract class GetBackupStatusUseCase : KotlinResultUseCase<UseCase.None, BackupStatusModel>()

abstract class ConnectGoogleDriveUseCase : KotlinResultUseCase<UseCase.None, BackupStatusModel>()

abstract class DisconnectGoogleDriveUseCase : KotlinResultUseCase<UseCase.None, UseCase.Empty>()

abstract class CreateBackupUseCase : KotlinResultUseCase<UseCase.None, UseCase.Empty>()

abstract class RestoreBackupUseCase : KotlinResultUseCase<UseCase.None, UseCase.Empty>()
