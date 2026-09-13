package wottrich.github.io.smartchecklist.presentation.state

import wottrich.github.io.smartchecklist.domain.model.SortItemType
import wottrich.github.io.smartchecklist.presentation.task.model.BaseTaskListItem
import wottrich.github.io.smartchecklist.presentation.ui.TaskBottomSheetType

data class TaskComponentUiState(
    val tasks: List<BaseTaskListItem> = emptyList(),
    val taskName: String = "",
    val selectedSortItem: SortItemType = SortItemType.UNSELECTED_SORT,
    val taskBottomSheetType: TaskBottomSheetType? = null,
)