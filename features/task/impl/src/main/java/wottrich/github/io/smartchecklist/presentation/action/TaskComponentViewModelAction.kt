package wottrich.github.io.smartchecklist.presentation.action

import wottrich.github.io.smartchecklist.datasource.data.model.Task

interface TaskComponentViewModelAction {
    fun sendAction(action: Action)

    sealed class Action {
        object AddTask : Action()
        data class ChangeTaskStatus(val task: Task) : Action()
        data class DeleteTask(val task: Task) : Action()
        data class OnTextChanged(val text: String) : Action()
    }
}