package wottrich.github.io.smartchecklist.backup.presentation.viewmodel

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.smartchecklist.backup.R
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.backup.data.repository.BackupStatusModel
import wottrich.github.io.smartchecklist.backup.domain.BackupError
import wottrich.github.io.smartchecklist.backup.domain.ConnectGoogleDriveUseCase
import wottrich.github.io.smartchecklist.backup.domain.CreateBackupUseCase
import wottrich.github.io.smartchecklist.backup.domain.DisconnectGoogleDriveUseCase
import wottrich.github.io.smartchecklist.backup.domain.GetBackupStatusUseCase
import wottrich.github.io.smartchecklist.backup.domain.RestoreBackupUseCase
import wottrich.github.io.smartchecklist.backup.google.GoogleDriveAuthorization
import wottrich.github.io.smartchecklist.backup.presentation.state.BackupUiActions
import wottrich.github.io.smartchecklist.backup.presentation.state.BackupUiEffects
import wottrich.github.io.smartchecklist.backup.presentation.state.BackupUiState
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus

/**
 * Consent seam: [BackupRepositoryImpl] cannot carry Android types, so on
 * `BackupError.NeedsConsent` the ViewModel asks [GoogleDriveAuthorization] directly
 * (both live in this impl module) for the resolvable consent intent and emits
 * [BackupUiEffects.RequestConsent]. The screen launches it and re-sends
 * [BackupUiActions.Action.ConnectAction] on success.
 */
class BackupViewModel(
    dispatchersProviders: DispatchersProviders,
    private val getBackupStatusUseCase: GetBackupStatusUseCase,
    private val connectGoogleDriveUseCase: ConnectGoogleDriveUseCase,
    private val disconnectGoogleDriveUseCase: DisconnectGoogleDriveUseCase,
    private val createBackupUseCase: CreateBackupUseCase,
    private val restoreBackupUseCase: RestoreBackupUseCase,
    private val googleDriveAuthorization: GoogleDriveAuthorization,
) : BaseViewModel(dispatchersProviders), BackupUiActions {

    private val _uiState = MutableStateFlow<BackupUiState>(BackupUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiEffects = SingleShotEventBus<BackupUiEffects>()
    val uiEffects = _uiEffects.events

    init {
        launchIO {
            getBackupStatusUseCase().onSuccess { status ->
                showOverview(status.connectedAccountEmail, status.lastBackupDate)
            }
        }
    }

    override fun sendAction(action: BackupUiActions.Action) {
        when (action) {
            BackupUiActions.Action.ConnectAction -> onConnectAction()
            BackupUiActions.Action.DisconnectAction -> onDisconnectAction()
            BackupUiActions.Action.BackupNowAction -> onBackupNowAction()
            BackupUiActions.Action.RestoreAction -> onRestoreAction()
        }
    }

    private fun onConnectAction() {
        setWorking(true)
        launchIO {
            connectGoogleDriveUseCase()
                .onSuccess { status ->
                    showOverview(status.connectedAccountEmail, status.lastBackupDate, isWorking = false)
                }
                .onFailure { exception ->
                    if (exception is BackupError.NeedsConsent) {
                        requestConsent()
                    } else {
                        handleFailure(exception)
                    }
                }
        }
    }

    private suspend fun requestConsent() {
        setWorking(false)
        when (val outcome = googleDriveAuthorization.authorize()) {
            is GoogleDriveAuthorization.AuthorizationOutcome.NeedsConsent ->
                _uiEffects.emit(BackupUiEffects.RequestConsent(outcome.resolvablePendingIntent))

            else -> _uiEffects.emit(BackupUiEffects.SnackbarError(R.string.backup_error_drive_io))
        }
    }

    private fun onDisconnectAction() {
        setWorking(true)
        launchIO {
            disconnectGoogleDriveUseCase()
                .onSuccess {
                    showOverview(connectedAccountEmail = null, lastBackupDate = null, isWorking = false)
                    _uiEffects.emit(BackupUiEffects.DisconnectCompleted)
                }
                .onFailure { exception -> handleFailure(exception) }
        }
    }

    private fun onBackupNowAction() {
        setWorking(true)
        launchIO {
            createBackupUseCase()
                .onSuccess {
                    refreshOverview()
                    _uiEffects.emit(BackupUiEffects.BackupCompleted)
                }
                .onFailure { exception -> handleFailure(exception) }
        }
    }

    private fun onRestoreAction() {
        setWorking(true)
        launchIO {
            restoreBackupUseCase()
                .onSuccess {
                    refreshOverview()
                    _uiEffects.emit(BackupUiEffects.RestoreCompleted)
                }
                .onFailure { exception -> handleFailure(exception) }
        }
    }

    /** Re-reads the persisted status and renders it in the overview state. */
    private suspend fun refreshOverview() {
        var status: BackupStatusModel? = null
        getBackupStatusUseCase().onSuccess { status = it }
        status?.let { showOverview(it.connectedAccountEmail, it.lastBackupDate, isWorking = false) }
    }

    private fun showOverview(
        connectedAccountEmail: String?,
        lastBackupDate: Long?,
        isWorking: Boolean = false,
    ) {
        _uiState.value = BackupUiState.Overview(
            connectedAccountEmail = connectedAccountEmail,
            lastBackupDate = lastBackupDate,
            isWorking = isWorking,
        )
    }

    private fun setWorking(isWorking: Boolean) {
        val state = _uiState.value
        if (state is BackupUiState.Overview) {
            _uiState.value = state.copy(isWorking = isWorking)
        }
    }

    private suspend fun handleFailure(exception: Throwable) {
        setWorking(false)
        _uiEffects.emit(BackupUiEffects.SnackbarError(exception.mapToStringRes()))
    }

    private fun Throwable.mapToStringRes(): Int = when (this) {
        is BackupError.NotConnected -> R.string.backup_error_not_connected
        is BackupError.NeedsConsent -> R.string.backup_error_not_connected
        is BackupError.NoBackupFound -> R.string.backup_error_no_backup_found
        is BackupError.CorruptedBackupFile -> R.string.backup_error_corrupted_backup
        is BackupError.DriveIoError -> R.string.backup_error_drive_io
        else -> R.string.backup_error_drive_io
    }
}
