package wottrich.github.io.smartchecklist.serversync.presentation.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus
import wottrich.github.io.smartchecklist.serversync.R
import wottrich.github.io.smartchecklist.serversync.api.ServerSyncRepository
import wottrich.github.io.smartchecklist.serversync.domain.usecase.DisconnectServerUseCase
import wottrich.github.io.smartchecklist.serversync.domain.usecase.RegisterDeviceUseCase
import wottrich.github.io.smartchecklist.serversync.domain.usecase.SyncNowUseCase
import wottrich.github.io.smartchecklist.serversync.domain.usecase.TestConnectionUseCase
import wottrich.github.io.smartchecklist.serversync.model.ServerConnectionState
import wottrich.github.io.smartchecklist.serversync.presentation.state.ServerSettingsUiActions
import wottrich.github.io.smartchecklist.serversync.presentation.state.ServerSettingsUiEffects
import wottrich.github.io.smartchecklist.serversync.presentation.state.ServerSettingsUiState

class ServerSettingsViewModel(
    dispatchersProviders: DispatchersProviders,
    private val serverSyncRepository: ServerSyncRepository,
    private val syncNowUseCase: SyncNowUseCase,
    private val testConnectionUseCase: TestConnectionUseCase,
    private val registerDeviceUseCase: RegisterDeviceUseCase,
    private val disconnectServerUseCase: DisconnectServerUseCase,
) : BaseViewModel(dispatchersProviders), ServerSettingsUiActions {

    private val _uiState = MutableStateFlow<ServerSettingsUiState>(ServerSettingsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiEffects = SingleShotEventBus<ServerSettingsUiEffects>()
    val uiEffects = _uiEffects.events

    init {
        launchIO {
            refreshOverview()
        }
    }

    override fun sendAction(action: ServerSettingsUiActions.Action) {
        when (action) {
            is ServerSettingsUiActions.Action.RegisterAction -> onRegisterAction(action)
            ServerSettingsUiActions.Action.TestConnectionAction -> onTestConnectionAction()
            ServerSettingsUiActions.Action.DisconnectAction -> onDisconnectAction()
            ServerSettingsUiActions.Action.SyncNowAction -> onSyncNowAction()
        }
    }

    private fun onRegisterAction(action: ServerSettingsUiActions.Action.RegisterAction) {
        setWorking(true)
        launchIO {
            registerDeviceUseCase(action.serverUrl)
                .onSuccess { state ->
                    refreshOverview()
                    _uiEffects.emit(ServerSettingsUiEffects.ShowSnackbar(state.mapToStringRes()))
                }
                .onFailure { setWorking(false) }
        }
    }

    private fun onTestConnectionAction() {
        setWorking(true)
        launchIO {
            testConnectionUseCase()
                .onSuccess { state ->
                    setWorking(false)
                    _uiEffects.emit(ServerSettingsUiEffects.ShowSnackbar(state.mapToStringRes()))
                }
                .onFailure { setWorking(false) }
        }
    }

    private fun onDisconnectAction() {
        setWorking(true)
        launchIO {
            disconnectServerUseCase()
                .onSuccess {
                    refreshOverview()
                    _uiEffects.emit(
                        ServerSettingsUiEffects.ShowSnackbar(R.string.server_sync_disconnected)
                    )
                }
                .onFailure { setWorking(false) }
        }
    }

    private fun onSyncNowAction() {
        setWorking(true)
        launchIO {
            syncNowUseCase()
                .onSuccess {
                    refreshOverview()
                    _uiEffects.emit(
                        ServerSettingsUiEffects.ShowSnackbar(R.string.server_sync_sync_completed)
                    )
                }
                .onFailure {
                    refreshOverview()
                    _uiEffects.emit(
                        ServerSettingsUiEffects.ShowSnackbar(R.string.server_sync_sync_failed)
                    )
                }
        }
    }

    private suspend fun refreshOverview() {
        val status = serverSyncRepository.getSyncStatus()
        _uiState.value = ServerSettingsUiState.Overview(
            isConfigured = status.isConfigured,
            serverUrl = status.serverUrl,
            lastSyncAt = status.lastSyncAt,
            pendingChanges = status.pendingChanges,
            lastErrorKind = status.lastErrorKind,
            isWorking = false,
        )
    }

    private fun setWorking(isWorking: Boolean) {
        val state = _uiState.value
        if (state is ServerSettingsUiState.Overview) {
            _uiState.value = state.copy(isWorking = isWorking)
        }
    }

    private fun ServerConnectionState.mapToStringRes(): Int = when (this) {
        ServerConnectionState.NotConfigured -> R.string.server_sync_connection_not_configured
        ServerConnectionState.Unauthorized -> R.string.server_sync_connection_unauthorized
        ServerConnectionState.Unreachable -> R.string.server_sync_connection_unreachable
        is ServerConnectionState.Connected -> R.string.server_sync_connection_connected
    }
}
