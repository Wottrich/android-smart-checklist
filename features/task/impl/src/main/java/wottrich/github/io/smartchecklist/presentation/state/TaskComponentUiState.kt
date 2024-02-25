package wottrich.github.io.smartchecklist.presentation.state

import wottrich.github.io.smartchecklist.domain.model.SortItemType

data class TaskComponentUiState(
    val taskName: String = "",
    val selectedSortItem: SortItemType = SortItemType.UNSELECTED_SORT
)