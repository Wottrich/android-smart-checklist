package wottrich.github.io.smartchecklist.quicklychecklist.presentation.states

import wottrich.github.io.smartchecklist.datasource.entity.NewTask

data class QuicklyChecklistUiState(
    val tasks: List<NewTask> = listOf()
)