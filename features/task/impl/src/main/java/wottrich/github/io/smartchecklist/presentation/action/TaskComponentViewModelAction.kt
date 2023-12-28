package wottrich.github.io.smartchecklist.presentation.action

import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.presentation.sort.model.TaskSortItemState

interface TaskComponentViewModelAction {
    fun sendAction(action: Action)

    sealed class Action {
        object AddTask : Action()
        data class ChangeTaskStatus(val task: Task) : Action()
        data class DeleteTask(val task: Task) : Action()
        data class OnTextChanged(val text: String) : Action()
        data class OnSortItemClicked(val sortItemState: TaskSortItemState) : Action()
    }
}