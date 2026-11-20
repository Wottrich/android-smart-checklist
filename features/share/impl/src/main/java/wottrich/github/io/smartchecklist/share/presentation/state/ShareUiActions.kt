package wottrich.github.io.smartchecklist.share.presentation.state

interface ShareUiActions {

    fun sendAction(action: Action)

    sealed class Action {
        /** Starts hosting the currently selected checklist. */
        data object StartSharingAction : Action()

        /** Stops hosting; guests keep a frozen local copy. */
        data object StopSharingAction : Action()

        /** Revokes every paired guest and rotates the pairing code (v2 hardening). */
        data object ResetPairingAction : Action()
    }
}
