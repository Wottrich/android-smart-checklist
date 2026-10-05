package wottrich.github.io.smartchecklist.backup.presentation.state

interface BackupUiActions {

    fun sendAction(action: Action)

    sealed class Action {
        data object ConnectAction : Action()
        data object DisconnectAction : Action()
        data object BackupNowAction : Action()
        data object RestoreAction : Action()
    }
}
