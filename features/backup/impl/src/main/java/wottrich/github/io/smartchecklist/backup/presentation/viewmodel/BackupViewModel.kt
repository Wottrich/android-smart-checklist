package wottrich.github.io.smartchecklist.backup.presentation.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.backup.R
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
 * Consent seam: the public `BackupRepository` contract cannot carry Android types,
 * so on `BackupError.NeedsConsent` the ViewModel asks the impl-level
 * [GoogleDriveAuthorization] directly for the resolvable consent intent and emits
 * [BackupUiEffects.RequestConsent]. When the consent screen returns, the result
 * data is consumed through [GoogleDriveAuthorization.completeConsent] and the
 * interrupted action is re-dispatched against the repository.
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

    /** Action interrupted by the consent flow, re-dispatched after consent completes. */
    private var actionPendingConsent: BackupUiActions.Action = BackupUiActions.Action.ConnectAction

    init {
        launchIO {
            getBackupStatusUseCase().onSuccess { status ->
                showOverview(status)
            }
        }
    }

    override fun sendAction(action: BackupUiActions.Action) {
        when (action) {
            BackupUiActions.Action.ConnectAction -> onConnectAction()
            BackupUiActions.Action.DisconnectAction -> onDisconnectAction()
            BackupUiActions.Action.BackupNowAction -> onBackupNowAction()
            BackupUiActions.Action.RestoreAction -> onRestoreAction()
            is BackupUiActions.Action.ConsentCompletedAction -> onConsentCompletedAction(action)
            BackupUiActions.Action.ConsentCanceledAction -> onConsentCanceledAction()
        }
    }

    private fun onConnectAction() {
        setWorking(true)
        launchIO {
            connectGoogleDriveUseCase()
                .onSuccess { status ->
                    showOverview(status)
                }
                .onFailure { exception ->
                    handleFailureOrConsent(exception, BackupUiActions.Action.ConnectAction)
                }
        }
    }

    /**
     * Asks [GoogleDriveAuthorization] for the resolvable consent intent and emits
     * [BackupUiEffects.RequestConsent]. [followUpAction] is re-dispatched once the
     * consent result is consumed ([BackupUiActions.Action.ConsentCompletedAction]).
     */
    private suspend fun requestConsent(followUpAction: BackupUiActions.Action) {
        setWorking(false)
        actionPendingConsent = followUpAction
        when (val outcome = googleDriveAuthorization.authorize()) {
            is GoogleDriveAuthorization.AuthorizationOutcome.NeedsConsent ->
                _uiEffects.emit(BackupUiEffects.RequestConsent(outcome.resolvablePendingIntent))

            // Consent is already granted (e.g. silent token refresh): no UI needed.
            else -> sendAction(followUpAction)
        }
    }

    /**
     * Consumes the consent activity result — the access token comes from this
     * result (`getAuthorizationResultFromIntent`), never from calling `authorize`
     * again, or Play services would keep answering `hasResolution() == true`.
     */
    private fun onConsentCompletedAction(action: BackupUiActions.Action.ConsentCompletedAction) {
        launchIO {
            when (val outcome = googleDriveAuthorization.completeConsent(action.consentResultIntent)) {
                is GoogleDriveAuthorization.AuthorizationOutcome.Granted ->
                    sendAction(actionPendingConsent)

                is GoogleDriveAuthorization.AuthorizationOutcome.NeedsConsent ->
                    _uiEffects.emit(BackupUiEffects.RequestConsent(outcome.resolvablePendingIntent))

                is GoogleDriveAuthorization.AuthorizationOutcome.Failed ->
                    handleFailure(outcome.exception)
            }
        }
    }

    /** Consent screen closed without granting (user choice or a GMS-side error). */
    private fun onConsentCanceledAction() {
        setWorking(false)
        launchIO {
            _uiEffects.emit(BackupUiEffects.ShowSnackbar(R.string.backup_connect_canceled))
        }
    }

    private fun onDisconnectAction() {
        setWorking(true)
        launchIO {
            disconnectGoogleDriveUseCase()
                .onSuccess {
                    showOverview(BackupStatusModel(isConnected = false, connectedAccountEmail = null, lastBackupDate = null))
                    _uiEffects.emit(BackupUiEffects.ShowSnackbar(R.string.backup_disconnected))
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
                    _uiEffects.emit(BackupUiEffects.ShowSnackbar(R.string.backup_backup_completed))
                }
                .onFailure { exception -> handleFailureOrConsent(exception, BackupUiActions.Action.BackupNowAction) }
        }
    }

    private fun onRestoreAction() {
        setWorking(true)
        launchIO {
            restoreBackupUseCase()
                .onSuccess {
                    refreshOverview()
                    _uiEffects.emit(BackupUiEffects.ShowSnackbar(R.string.backup_restore_completed))
                }
                .onFailure { exception -> handleFailureOrConsent(exception, BackupUiActions.Action.RestoreAction) }
        }
    }

    /** Re-reads the persisted status and renders it in the overview state. */
    private suspend fun refreshOverview() {
        getBackupStatusUseCase().onSuccess { status ->
            showOverview(status)
        }
    }

    private fun showOverview(
        status: BackupStatusModel,
        isWorking: Boolean = false,
    ) {
        _uiState.value = BackupUiState.Overview(
            isConnected = status.isConnected,
            connectedAccountEmail = status.connectedAccountEmail,
            lastBackupDate = status.lastBackupDate,
            isWorking = isWorking,
        )
    }

    private fun setWorking(isWorking: Boolean) {
        val state = _uiState.value
        if (state is BackupUiState.Overview) {
            _uiState.value = state.copy(isWorking = isWorking)
        }
    }

    private suspend fun handleFailureOrConsent(
        exception: Throwable,
        followUpAction: BackupUiActions.Action,
    ) {
        if (exception is BackupError.NeedsConsent) {
            requestConsent(followUpAction)
        } else {
            handleFailure(exception)
        }
    }

    private suspend fun handleFailure(exception: Throwable) {
        setWorking(false)
        _uiEffects.emit(BackupUiEffects.ShowSnackbar(exception.mapToStringRes()))
    }

    private fun Throwable.mapToStringRes(): Int = when (this) {
        is BackupError.NeedsConsent -> R.string.backup_error_not_connected
        is BackupError.NoBackupFound -> R.string.backup_error_no_backup_found
        is BackupError.CorruptedBackupFile -> R.string.backup_error_corrupted_backup
        else -> R.string.backup_error_drive_io
    }
}
