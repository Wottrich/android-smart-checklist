package wottrich.github.io.smartchecklist.share.presentation.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.checklist.data.repository.ChecklistRepository
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus
import wottrich.github.io.smartchecklist.share.R
import wottrich.github.io.smartchecklist.share.api.ShareRepository
import wottrich.github.io.smartchecklist.share.presentation.state.ShareUiActions
import wottrich.github.io.smartchecklist.share.presentation.state.ShareUiEffects
import wottrich.github.io.smartchecklist.share.presentation.state.ShareUiState

/**
 * Owner-side share screen: flag/unflag the selected checklist. The heavy lifting
 * (host lifecycle, loopback registration, per-host routing) lives in
 * [wottrich.github.io.smartchecklist.share.data.ShareRepositoryImpl].
 */
class ShareViewModel(
    dispatchersProviders: DispatchersProviders,
    private val shareRepository: ShareRepository,
    private val checklistRepository: ChecklistRepository,
) : BaseViewModel(dispatchersProviders), ShareUiActions {

    private val _uiState = MutableStateFlow<ShareUiState>(ShareUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiEffects = SingleShotEventBus<ShareUiEffects>()
    val uiEffects = _uiEffects.events

    init {
        launchIO {
            refreshOverview()
        }
    }

    override fun sendAction(action: ShareUiActions.Action) {
        when (action) {
            ShareUiActions.Action.StartSharingAction -> onStartSharingAction()
            ShareUiActions.Action.StopSharingAction -> onStopSharingAction()
            ShareUiActions.Action.ResetPairingAction -> onResetPairingAction()
        }
    }

    private fun onResetPairingAction() {
        setWorking(true)
        launchIO {
            shareRepository.resetPairing()
                .onSuccess {
                    refreshOverview()
                    _uiEffects.emit(ShareUiEffects.ShowSnackbar(R.string.pair_reset_done))
                }
                .onFailure {
                    setWorking(false)
                    _uiEffects.emit(ShareUiEffects.ShowSnackbar(R.string.share_failed))
                }
        }
    }

    private fun onStartSharingAction() {
        setWorking(true)
        launchIO {
            val checklistUuid = currentChecklistUuid() ?: run {
                setWorking(false)
                return@launchIO
            }
            shareRepository.share(checklistUuid)
                .onSuccess {
                    refreshOverview()
                    _uiEffects.emit(ShareUiEffects.ShowSnackbar(R.string.share_started))
                }
                .onFailure {
                    refreshOverview()
                    _uiEffects.emit(ShareUiEffects.ShowSnackbar(R.string.share_failed))
                }
        }
    }

    private fun onStopSharingAction() {
        setWorking(true)
        launchIO {
            val checklistUuid = currentChecklistUuid() ?: run {
                setWorking(false)
                return@launchIO
            }
            val shareId = shareRepository.observeSharedChecklists().first()
                .firstOrNull { it.checklistUuid == checklistUuid }?.shareId
            if (shareId == null) {
                refreshOverview()
                setWorking(false)
                return@launchIO
            }
            shareRepository.stopSharing(shareId)
                .onSuccess {
                    refreshOverview()
                    _uiEffects.emit(ShareUiEffects.ShowSnackbar(R.string.share_stopped))
                }
                .onFailure {
                    refreshOverview()
                    _uiEffects.emit(ShareUiEffects.ShowSnackbar(R.string.share_failed))
                }
        }
    }

    private suspend fun refreshOverview() {
        val checklist = checklistRepository.getSelectedChecklist()
        val shared = shareRepository.observeSharedChecklists().first()
            .firstOrNull { it.checklistUuid == checklist?.uuid }
        val status = shareRepository.getHostStatus()
        _uiState.value = ShareUiState.Overview(
            checklistName = checklist?.name.orEmpty(),
            isShared = shared != null,
            isHosting = status.isHosting,
            deviceName = status.deviceName,
            guestAddress = status.guestAddress,
            guestCount = status.guestCount,
            pairingCode = shareRepository.currentPairingCode(),
            pairingRotationInMs = shareRepository.msUntilCodeRotation(),
            isWorking = false,
        )
    }

    private suspend fun currentChecklistUuid(): String? =
        checklistRepository.getSelectedChecklist()?.uuid

    private fun setWorking(isWorking: Boolean) {
        val state = _uiState.value
        if (state is ShareUiState.Overview) {
            _uiState.value = state.copy(isWorking = isWorking)
        }
    }
}
