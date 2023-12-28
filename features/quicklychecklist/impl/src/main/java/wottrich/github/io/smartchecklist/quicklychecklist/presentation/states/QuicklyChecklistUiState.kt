package wottrich.github.io.smartchecklist.quicklychecklist.presentation.states

import wottrich.github.io.smartchecklist.datasource.data.model.Task

data class QuicklyChecklistUiState(
    val tasks: List<Task> = listOf()
)