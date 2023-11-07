package wottrich.github.io.smartchecklist.presentation.state

import wottrich.github.io.smartchecklist.presentation.sort.model.TaskSortItemState

data class TaskComponentUiState(
    val taskName: String = "",
    val sortItems: List<TaskSortItemState> = listOf()
)