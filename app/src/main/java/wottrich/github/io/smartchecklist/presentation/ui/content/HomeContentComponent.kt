package wottrich.github.io.smartchecklist.presentation.ui.content

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.presentation.state.HomeState
import wottrich.github.io.smartchecklist.presentation.state.HomeUiState
import wottrich.github.io.smartchecklist.presentation.ui.TaskContentComponent

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 01/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@Composable
fun HomeContentComponent(
    checklistState: HomeState,
    onUpdateItemClicked: (Task) -> Unit,
    onError: (Int) -> Unit,
    onTaskCounterClicked: () -> Unit,
    onNewChecklistClicked: () -> Unit
) {
    when (checklistState.homeUiState) {
        is HomeUiState.Overview -> TaskContentComponent(
            showHeaderComponent = checklistState.isEditUiState,
            showDeleteIcon = checklistState.isEditUiState,
            showSortComponent = checklistState.isEditUiState,
            onUpdateClicked = onUpdateItemClicked,
            onError = onError,
            onTaskCounterClicked = onTaskCounterClicked
        )

        is HomeUiState.Empty -> HomeEmptyStateComponent(onNewChecklistClicked)
        HomeUiState.Loading -> CircularProgressIndicator()
    }
}