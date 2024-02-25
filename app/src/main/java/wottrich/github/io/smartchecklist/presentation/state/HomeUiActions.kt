package wottrich.github.io.smartchecklist.presentation.state

import androidx.annotation.StringRes
import wottrich.github.io.smartchecklist.datasource.data.model.Task

interface HomeUiActions {
    fun sendAction(action: Action)

    sealed class Action {
        object OnChangeEditModeAction : Action()
        data class OnShowTaskChangeStatusSnackbar(val task: Task) : Action()
        object DeleteChecklistAction : Action()
        data class OnSnackbarError(@StringRes val message: Int) : Action()
    }
}