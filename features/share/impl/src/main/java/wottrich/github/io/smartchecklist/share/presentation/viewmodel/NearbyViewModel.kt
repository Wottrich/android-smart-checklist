package wottrich.github.io.smartchecklist.share.presentation.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus
import wottrich.github.io.smartchecklist.share.R
import wottrich.github.io.smartchecklist.share.api.NearbyHost
import wottrich.github.io.smartchecklist.share.api.ShareOffer
import wottrich.github.io.smartchecklist.share.api.ShareRepository
import wottrich.github.io.smartchecklist.share.domain.ShareError
import wottrich.github.io.smartchecklist.share.presentation.state.NearbyUiActions
import wottrich.github.io.smartchecklist.share.presentation.state.NearbyUiEffects
import wottrich.github.io.smartchecklist.share.presentation.state.NearbyUiState

/**
 * Guest screen: scan the Wi-Fi for share-hosting devices, inspect a host's offers
 * and accept checklists individually (Phase E).
 */
class NearbyViewModel(
    dispatchersProviders: DispatchersProviders,
    private val shareRepository: ShareRepository,
) : BaseViewModel(dispatchersProviders), NearbyUiActions {

    private val _uiState = MutableStateFlow<NearbyUiState>(NearbyUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiEffects = SingleShotEventBus<NearbyUiEffects>()
    val uiEffects = _uiEffects.events

    init {
        sendAction(NearbyUiActions.Action.ScanAction)
    }

    override fun sendAction(action: NearbyUiActions.Action) {
        when (action) {
            NearbyUiActions.Action.ScanAction -> onScanAction()
            is NearbyUiActions.Action.SelectHostAction -> onSelectHostAction(action.host)
            is NearbyUiActions.Action.AcceptAction -> onAcceptAction(action.offer, action.host)
            is NearbyUiActions.Action.AddManualHostAction -> onAddManualHostAction(action)
            is NearbyUiActions.Action.PairAction -> onPairAction(action)
        }
    }

    private fun onPairAction(action: NearbyUiActions.Action.PairAction) {
        update { it.copy(isWorking = true) }
        launchIO {
            shareRepository.pairWithCode(action.host, action.code)
                .onSuccess {
                    update { it.copy(isWorking = false, pairingHost = null) }
                    // paired → offers are now visible for this host
                    onSelectHostAction(action.host)
                }
                .onFailure { error ->
                    update { it.copy(isWorking = false) }
                    val message = when ((error as? ShareError)?.kind) {
                        "too_many_attempts" -> R.string.pair_locked_out
                        else -> R.string.pair_failed
                    }
                    _uiEffects.emit(NearbyUiEffects.ShowSnackbar(message))
                }
        }
    }

    private fun onScanAction() {
        update { it.copy(isScanning = true, isWorking = true) }
        launchIO {
            val hosts = shareRepository.scanNearbyHosts()
            update { it.copy(isScanning = false, isWorking = false, hosts = hosts, selectedHost = null, offers = emptyList()) }
        }
    }

    private fun onSelectHostAction(host: NearbyHost) {
        update { it.copy(selectedHost = host, isLoadingOffers = true, offers = emptyList()) }
        launchIO {
            shareRepository.fetchOffers(host)
                .onSuccess { offers ->
                    update { it.copy(isLoadingOffers = false, offers = offers) }
                }
                .onFailure { error ->
                    update { it.copy(isLoadingOffers = false) }
                    if ((error as? ShareError)?.kind == "pairing_required") {
                        // gated host — open the pairing-code dialog
                        update { it.copy(pairingHost = host) }
                    } else {
                        _uiEffects.emit(NearbyUiEffects.ShowSnackbar(R.string.nearby_offers_failed))
                    }
                }
        }
    }

    private fun onAcceptAction(offer: ShareOffer, host: NearbyHost) {
        update { it.copy(acceptingShareId = offer.shareId, isWorking = true) }
        launchIO {
            shareRepository.accept(offer, host)
                .onSuccess {
                    // accepted checklist materialized locally — drop it from the offers list
                    update { state ->
                        state.copy(
                            acceptingShareId = null,
                            isWorking = false,
                            offers = state.offers.filterNot { it.shareId == offer.shareId },
                        )
                    }
                    _uiEffects.emit(NearbyUiEffects.ShowSnackbar(R.string.nearby_accepted))
                }
                .onFailure {
                    update { it.copy(acceptingShareId = null, isWorking = false) }
                    _uiEffects.emit(NearbyUiEffects.ShowSnackbar(R.string.nearby_accept_failed))
                }
        }
    }

    private fun onAddManualHostAction(action: NearbyUiActions.Action.AddManualHostAction) {
        update { it.copy(isWorking = true) }
        launchIO {
            shareRepository.addManualHost(action.address)
                .onSuccess { addedHost ->
                    val hosts = shareRepository.scanNearbyHosts()
                    update { it.copy(isWorking = false, hosts = hosts) }
                    onSelectHostAction(addedHost)
                }
                .onFailure {
                    update { it.copy(isWorking = false) }
                    _uiEffects.emit(NearbyUiEffects.ShowSnackbar(R.string.nearby_manual_invalid))
                }
        }
    }

    /** Closes the pairing dialog without pairing. */
    fun dismissPairing() {
        update { it.copy(pairingHost = null) }
    }

    private fun update(reducer: (NearbyUiState.Overview) -> NearbyUiState.Overview) {
        val state = _uiState.value
        _uiState.value = if (state is NearbyUiState.Overview) {
            reducer(state)
        } else {
            NearbyUiState.Overview(
                isScanning = true,
                hosts = emptyList(),
                selectedHost = null,
                pairingHost = null,
                offers = emptyList(),
                isLoadingOffers = false,
                acceptingShareId = null,
                isWorking = false,
            )
        }
    }
}
