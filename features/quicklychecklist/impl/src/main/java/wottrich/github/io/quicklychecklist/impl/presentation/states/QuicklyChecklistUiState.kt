package wottrich.github.io.quicklychecklist.impl.presentation.states

import wottrich.github.io.datasource.entity.NewTask

data class QuicklyChecklistUiState(
    val tasks: List<NewTask> = listOf()
)