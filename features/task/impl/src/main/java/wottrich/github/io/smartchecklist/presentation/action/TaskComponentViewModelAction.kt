package wottrich.github.io.smartchecklist.presentation.action

import wottrich.github.io.smartchecklist.datasource.entity.NewTask

interface TaskComponentViewModelAction {
    fun sendAction(action: Action)

    sealed class Action {
        object AddTask : Action()
        data class ChangeTaskStatus(val task: NewTask) : Action()
        data class DeleteTask(val task: NewTask) : Action()
        data class OnTextChanged(val text: String) : Action()
    }
}