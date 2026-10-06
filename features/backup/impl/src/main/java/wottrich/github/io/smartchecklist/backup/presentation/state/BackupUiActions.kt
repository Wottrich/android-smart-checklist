package wottrich.github.io.smartchecklist.backup.presentation.state

import android.content.Intent

interface BackupUiActions {

    fun sendAction(action: Action)

    sealed class Action {
        data object ConnectAction : Action()
        data object DisconnectAction : Action()
        data object BackupNowAction : Action()
        data object RestoreAction : Action()

        /** Carries the `onActivityResult` data of the consent screen (null = canceled). */
        data class ConsentCompletedAction(val consentResultIntent: Intent?) : Action()

        /** The consent screen was closed without completing (user back, or GMS error). */
        data object ConsentCanceledAction : Action()
    }
}
