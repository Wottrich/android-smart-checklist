package wottrich.github.io.smartchecklist.presentation.sort.model

import androidx.annotation.StringRes
import wottrich.github.io.smartchecklist.domain.model.SortItemType

data class TaskSortItemState(
    val type: SortItemType,
    @StringRes val labelRes: Int,
    val isSelected: Boolean
)