package wottrich.github.io.smartchecklist.presentation.state

import wottrich.github.io.smartchecklist.domain.model.TaskComponentModel
import wottrich.github.io.smartchecklist.presentation.ui.TaskBottomSheetType

data class TaskComponentUiState(
    val taskName: String = "",
    val checklist: TaskComponentModel? = null,
    val showSectionButton: Boolean = false,
    val taskBottomSheetType: TaskBottomSheetType? = null,
)