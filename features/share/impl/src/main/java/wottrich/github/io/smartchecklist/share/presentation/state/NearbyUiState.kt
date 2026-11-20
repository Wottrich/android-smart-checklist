package wottrich.github.io.smartchecklist.share.presentation.state

import wottrich.github.io.smartchecklist.share.api.NearbyHost
import wottrich.github.io.smartchecklist.share.api.ShareOffer

sealed class NearbyUiState {

    data object Loading : NearbyUiState()

    data class Overview(
        val isScanning: Boolean,
        val hosts: List<NearbyHost>,
        val selectedHost: NearbyHost?,
        /** Non-null while the pairing-code dialog is open for this host (v2). */
        val pairingHost: NearbyHost?,
        val offers: List<ShareOffer>,
        val isLoadingOffers: Boolean,
        val acceptingShareId: String?,
        val isWorking: Boolean,
    ) : NearbyUiState()
}
