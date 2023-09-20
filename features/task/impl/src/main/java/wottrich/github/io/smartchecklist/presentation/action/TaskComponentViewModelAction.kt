package wottrich.github.io.smartchecklist.presentation.action

import wottrich.github.io.smartchecklist.datasource.entity.NewTask
import wottrich.github.io.smartchecklist.presentation.sort.model.TaskSortItemState

interface TaskComponentViewModelAction {
    fun sendAction(action: Action)

    sealed class Action {
        object AddTask : Action()
        data class ChangeTaskStatus(val task: NewTask) : Action()
        data class DeleteTask(val task: NewTask) : Action()
        data class OnTextChanged(val text: String) : Action()
        data class OnSortItemClicked(val sortItemState: TaskSortItemState) : Action()
    }
}