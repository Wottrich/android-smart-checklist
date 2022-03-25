package wottrich.github.io.androidsmartchecklist.presentation.ui.content

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeUiState
import wottrich.github.io.database.entity.Task
import wottrich.github.io.publicandroid.presentation.ui.TaskContentComponent

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
    tasks: List<Task>,
    onAddItemClicked: (String) -> Unit,
    onUpdateItemClicked: (Task) -> Unit,
    onDeleteItemClicked: (Task) -> Unit,
    onNewChecklistClicked: () -> Unit
) {
    if (checklistState.homeUiState == HomeUiState.Loading) {
        CircularProgressIndicator()
    } else {
        when (checklistState.homeUiState) {
            is HomeUiState.Overview -> TaskContentComponent(
                tasks = tasks,
                showHeaderComponent = checklistState.isEditUiState,
                // TODO change to false to test swipe to delete as new way to delete tasks
                // In the future is a good way has some feature flag control
                showDeleteIcon = false/*checklistState.isEditUiState*/,
                onAddClicked = onAddItemClicked,
                onUpdateClicked = onUpdateItemClicked,
                onDeleteClicked = onDeleteItemClicked
            )
            is HomeUiState.Empty -> HomeEmptyStateComponent(onNewChecklistClicked)
            else -> Unit
        }
    }
}