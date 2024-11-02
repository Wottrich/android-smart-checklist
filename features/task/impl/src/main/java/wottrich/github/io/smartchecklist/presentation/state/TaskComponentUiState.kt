package wottrich.github.io.smartchecklist.presentation.state

import wottrich.github.io.smartchecklist.domain.model.SortItemType
import wottrich.github.io.smartchecklist.domain.model.TaskComponentModel

data class TaskComponentUiState(
    val taskName: String = "",
    val checklist: TaskComponentModel? = null,
    val showSectionButton: Boolean = false,
    val selectedSortItem: SortItemType = SortItemType.UNSELECTED_SORT
)