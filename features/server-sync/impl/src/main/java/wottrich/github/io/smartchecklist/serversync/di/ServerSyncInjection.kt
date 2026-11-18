package wottrich.github.io.smartchecklist.serversync.di

import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxDatasource
import wottrich.github.io.smartchecklist.serversync.api.KtorClientFactory
import wottrich.github.io.smartchecklist.serversync.api.ServerApi
import wottrich.github.io.smartchecklist.serversync.api.ServerApiImpl
import wottrich.github.io.smartchecklist.serversync.api.ServerSyncRepository
import wottrich.github.io.smartchecklist.serversync.api.SyncOutbox
import wottrich.github.io.smartchecklist.serversync.api.SyncScheduler
import wottrich.github.io.smartchecklist.serversync.data.ServerBackupFileSerializer
import wottrich.github.io.smartchecklist.serversync.data.ServerSettingsDatasource
import wottrich.github.io.smartchecklist.serversync.data.ServerSettingsDatasourceImpl
import wottrich.github.io.smartchecklist.serversync.data.SyncOutboxImpl
import wottrich.github.io.smartchecklist.serversync.data.repository.ServerBackupRepositoryImpl
import wottrich.github.io.smartchecklist.serversync.data.repository.ServerSyncRepositoryImpl
import wottrich.github.io.smartchecklist.serversync.data.repository.RoutingBackupRepository
import wottrich.github.io.smartchecklist.backup.data.repository.BackupRepository
import wottrich.github.io.smartchecklist.serversync.domain.usecase.DisconnectServerUseCase
import wottrich.github.io.smartchecklist.serversync.domain.usecase.DisconnectServerUseCaseImpl
import wottrich.github.io.smartchecklist.serversync.domain.usecase.RegisterDeviceUseCase
import wottrich.github.io.smartchecklist.serversync.domain.usecase.RegisterDeviceUseCaseImpl
import wottrich.github.io.smartchecklist.serversync.domain.usecase.SyncNowUseCase
import wottrich.github.io.smartchecklist.serversync.domain.usecase.SyncNowUseCaseImpl
import wottrich.github.io.smartchecklist.serversync.domain.usecase.TestConnectionUseCase
import wottrich.github.io.smartchecklist.serversync.domain.usecase.TestConnectionUseCaseImpl
import wottrich.github.io.smartchecklist.serversync.navigation.ServerSyncContextNavigator
import wottrich.github.io.smartchecklist.serversync.presentation.viewmodel.ServerSettingsViewModel
import wottrich.github.io.smartchecklist.serversync.work.WorkManagerSyncScheduler

val serverSyncModule = module {
    navigator()
    datasources()
    repository()
    useCases()
    viewModel {
        ServerSettingsViewModel(
            dispatchersProviders = get(),
            serverSyncRepository = get(),
            syncNowUseCase = get(),
            testConnectionUseCase = get(),
            registerDeviceUseCase = get(),
            disconnectServerUseCase = get()
        )
    }
}

private fun Module.navigator() {
    single { ServerSyncContextNavigator() } bind SmartChecklistNavigation::class
}

private fun Module.datasources() {
    single<ServerSettingsDatasource> { ServerSettingsDatasourceImpl(androidContext()) }
    single<SyncScheduler> { WorkManagerSyncScheduler(androidContext()) }
    single<HttpClient> { KtorClientFactory().create() }
    single<ServerApi> { ServerApiImpl(get()) }
    single<SyncOutbox> {
        SyncOutboxImpl(
            serverSettingsDatasource = get(),
            syncDatasource = get(),
            syncOutboxDatasource = get(),
            syncScheduler = get()
        )
    }
}

/**
 * Backup swap behind the server flag (Phase F): with server settings configured the
 * backup contract routes to the server; otherwise the original Drive impl, untouched.
 * `BackupInjection` binds the Drive impl under the "driveBackup" qualifier — this is
 * the single unqualified `BackupRepository` binding in the app.
 */
private fun Module.repository() {
    single<ServerSyncRepository> {
        ServerSyncRepositoryImpl(
            dispatchersProviders = get(),
            serverSettingsDatasource = get(),
            syncOutboxDatasource = get(),
            syncDatasource = get(),
            serverApi = get()
        )
    }
    factory<BackupRepository> {
        RoutingBackupRepository(
            serverSettingsDatasource = get(),
            driveBackupRepository = get(named("driveBackup")),
            serverBackupRepository = ServerBackupRepositoryImpl(
                dispatchersProviders = get(),
                serverSettingsDatasource = get(),
                serverApi = get(),
                checklistDatasource = get(),
                backupFileSerializer = ServerBackupFileSerializer()
            )
        )
    }
}

private fun Module.useCases() {
    factory<SyncNowUseCase> { SyncNowUseCaseImpl(get()) }
    factory<TestConnectionUseCase> { TestConnectionUseCaseImpl(get()) }
    factory<RegisterDeviceUseCase> { RegisterDeviceUseCaseImpl(get()) }
    factory<DisconnectServerUseCase> { DisconnectServerUseCaseImpl(get()) }
}
