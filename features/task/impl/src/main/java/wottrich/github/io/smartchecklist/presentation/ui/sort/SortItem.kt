package wottrich.github.io.smartchecklist.presentation.ui.sort

import wottrich.github.io.smartchecklist.domain.model.SortItemType

data class SortItem(
    val sortItemType: SortItemType,
    val isSelected: Boolean
)