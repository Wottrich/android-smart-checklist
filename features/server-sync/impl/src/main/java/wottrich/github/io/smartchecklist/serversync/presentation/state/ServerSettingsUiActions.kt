package wottrich.github.io.smartchecklist.serversync.presentation.state

interface ServerSettingsUiActions {

    fun sendAction(action: Action)

    sealed class Action {
        /** Pairs this device: registers a new identity at [serverUrl] and stores it. */
        data class RegisterAction(val serverUrl: String) : Action()

        /** Probe `/v1/auth/me` with the stored pairing. */
        data object TestConnectionAction : Action()

        /** Clears the pairing; never deletes local data. */
        data object DisconnectAction : Action()

        /** Manual sync attempt (push + pull now). */
        data object SyncNowAction : Action()
    }
}
