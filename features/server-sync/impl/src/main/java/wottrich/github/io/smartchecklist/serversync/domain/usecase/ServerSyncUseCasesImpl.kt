package wottrich.github.io.smartchecklist.serversync.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.serversync.api.ServerSyncRepository
import wottrich.github.io.smartchecklist.serversync.model.ServerConnectionState

internal class SyncNowUseCaseImpl(
    private val repository: ServerSyncRepository
) : SyncNowUseCase() {
    override suspend fun execute(params: UseCase.None): Result<UseCase.Empty> {
        return repository.syncNow()
    }
}

internal class TestConnectionUseCaseImpl(
    private val repository: ServerSyncRepository
) : TestConnectionUseCase() {
    override suspend fun execute(params: UseCase.None): Result<ServerConnectionState> {
        return repository.testConnection()
    }
}

internal class RegisterDeviceUseCaseImpl(
    private val repository: ServerSyncRepository
) : RegisterDeviceUseCase() {
    override suspend fun execute(params: String): Result<ServerConnectionState> {
        return repository.register(params)
    }
}

internal class DisconnectServerUseCaseImpl(
    private val repository: ServerSyncRepository
) : DisconnectServerUseCase() {
    override suspend fun execute(params: UseCase.None): Result<UseCase.Empty> {
        return repository.disconnect()
    }
}
