package wottrich.github.io.androidsmartchecklist.presentation.state

import wottrich.github.io.androidsmartchecklist.presentation.state.HomeUiState.Loading
import wottrich.github.io.androidsmartchecklist.presentation.state.HomeUiState.Overview
import wottrich.github.io.datasource.entity.NewChecklistWithNewTasks

data class HomeState(
    val homeUiState: HomeUiState,
    val checklistWithTasks: NewChecklistWithNewTasks?
) {
    val isEditUiState: Boolean
        get() = homeUiState is Overview && homeUiState.isEditing

    fun shouldShowActionContent(): Boolean {
        return homeUiState is Overview
    }

    companion object {
        val Initial = HomeState(Loading, null)
    }
}