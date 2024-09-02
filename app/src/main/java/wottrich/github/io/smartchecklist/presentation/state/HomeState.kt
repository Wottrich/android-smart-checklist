package wottrich.github.io.smartchecklist.presentation.state

import wottrich.github.io.smartchecklist.presentation.state.HomeUiState.Loading
import wottrich.github.io.smartchecklist.presentation.state.HomeUiState.Overview

data class HomeState(
    val homeUiState: HomeUiState,
    val checklistName: String?
) {
    val isEditUiState: Boolean
        get() = homeUiState is Overview && homeUiState.isEditing

    fun shouldShowActionContent(): Boolean {
        return homeUiState is Overview
    }

    companion object {
        val Initial = HomeState(
            homeUiState = Loading,
            checklistName = null
        )
    }
}