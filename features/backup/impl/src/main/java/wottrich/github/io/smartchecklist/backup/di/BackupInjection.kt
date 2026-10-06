package wottrich.github.io.smartchecklist.backup.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.backup.data.BackupPreferencesDatasource
import wottrich.github.io.smartchecklist.backup.data.BackupPreferencesDatasourceImpl
import wottrich.github.io.smartchecklist.backup.data.repository.BackupRepository
import wottrich.github.io.smartchecklist.backup.data.repository.BackupRepositoryImpl
import wottrich.github.io.smartchecklist.backup.data.serializer.BackupFileSerializer
import wottrich.github.io.smartchecklist.backup.data.serializer.BackupFileSerializerImpl
import wottrich.github.io.smartchecklist.backup.domain.ConnectGoogleDriveUseCase
import wottrich.github.io.smartchecklist.backup.domain.CreateBackupUseCase
import wottrich.github.io.smartchecklist.backup.domain.DisconnectGoogleDriveUseCase
import wottrich.github.io.smartchecklist.backup.domain.GetBackupStatusUseCase
import wottrich.github.io.smartchecklist.backup.domain.RestoreBackupUseCase
import wottrich.github.io.smartchecklist.backup.domain.usecase.ConnectGoogleDriveUseCaseImpl
import wottrich.github.io.smartchecklist.backup.domain.usecase.CreateBackupUseCaseImpl
import wottrich.github.io.smartchecklist.backup.domain.usecase.DisconnectGoogleDriveUseCaseImpl
import wottrich.github.io.smartchecklist.backup.domain.usecase.GetBackupStatusUseCaseImpl
import wottrich.github.io.smartchecklist.backup.domain.usecase.RestoreBackupUseCaseImpl
import wottrich.github.io.smartchecklist.backup.google.DriveBackupDatasource
import wottrich.github.io.smartchecklist.backup.google.DriveBackupDatasourceImpl
import wottrich.github.io.smartchecklist.backup.google.GoogleDriveAuthorization
import wottrich.github.io.smartchecklist.backup.google.GoogleDriveAuthorizationImpl
import wottrich.github.io.smartchecklist.backup.navigation.BackupContextNavigator
import wottrich.github.io.smartchecklist.backup.presentation.viewmodel.BackupViewModel

val backupModule = module {
    navigator()
    datasources()
    repository()
    useCases()
    viewModel {
        BackupViewModel(
            dispatchersProviders = get(),
            getBackupStatusUseCase = get(),
            connectGoogleDriveUseCase = get(),
            disconnectGoogleDriveUseCase = get(),
            createBackupUseCase = get(),
            restoreBackupUseCase = get(),
            googleDriveAuthorization = get()
        )
    }
}

private fun Module.navigator() {
    single { BackupContextNavigator() } bind SmartChecklistNavigation::class
}

private fun Module.datasources() {
    single<BackupPreferencesDatasource> { BackupPreferencesDatasourceImpl(androidContext()) }
    single<GoogleDriveAuthorization> {
        GoogleDriveAuthorizationImpl(
            context = androidContext(),
            dispatchersProviders = get()
        )
    }
    single<DriveBackupDatasource> { DriveBackupDatasourceImpl(get()) }
    factory<BackupFileSerializer> { BackupFileSerializerImpl() }
}

private fun Module.repository() {
    factory<BackupRepository> {
        BackupRepositoryImpl(
            dispatchersProviders = get(),
            checklistDatasource = get(),
            driveBackupDatasource = get(),
            googleDriveAuthorization = get(),
            backupPreferencesDatasource = get(),
            backupFileSerializer = get()
        )
    }
}

private fun Module.useCases() {
    factory<GetBackupStatusUseCase> { GetBackupStatusUseCaseImpl(get()) }
    factory<ConnectGoogleDriveUseCase> { ConnectGoogleDriveUseCaseImpl(get()) }
    factory<DisconnectGoogleDriveUseCase> { DisconnectGoogleDriveUseCaseImpl(get()) }
    factory<CreateBackupUseCase> { CreateBackupUseCaseImpl(get()) }
    factory<RestoreBackupUseCase> { RestoreBackupUseCaseImpl(get()) }
}
