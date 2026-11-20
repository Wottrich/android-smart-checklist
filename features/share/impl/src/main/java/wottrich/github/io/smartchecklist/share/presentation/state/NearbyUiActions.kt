package wottrich.github.io.smartchecklist.share.presentation.state

import wottrich.github.io.smartchecklist.share.api.NearbyHost
import wottrich.github.io.smartchecklist.share.api.ShareOffer

interface NearbyUiActions {

    fun sendAction(action: Action)

    sealed class Action {
        data object ScanAction : Action()
        data class SelectHostAction(val host: NearbyHost) : Action()
        data class AcceptAction(val offer: ShareOffer, val host: NearbyHost) : Action()
        data class AddManualHostAction(val address: String) : Action()

        /** Pairs this device with [host] using the owner's displayed code (v2). */
        data class PairAction(val host: NearbyHost, val code: String) : Action()
    }
}
