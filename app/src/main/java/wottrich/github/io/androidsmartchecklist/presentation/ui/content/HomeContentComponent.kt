package wottrich.github.io.androidsmartchecklist.presentation.ui.content

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeUiState
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.quicklychecklist.impl.presentation.ui.TaskContentComponent

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
    tasks: List<NewTask>,
    onAddItemClicked: (String) -> Unit,
    onUpdateItemClicked: (NewTask) -> Unit,
    onDeleteItemClicked: (NewTask) -> Unit,
    onNewChecklistClicked: () -> Unit
) {
    if (checklistState.homeUiState == HomeUiState.Loading) {
        CircularProgressIndicator()
    } else {
        when (checklistState.homeUiState) {
            is HomeUiState.Overview -> TaskContentComponent(
                tasks = tasks,
                showHeaderComponent = checklistState.isEditUiState,
                showDeleteIcon = checklistState.isEditUiState,
                onAddClicked = onAddItemClicked,
                onUpdateClicked = onUpdateItemClicked,
                onDeleteClicked = onDeleteItemClicked
            )
            is HomeUiState.Empty -> HomeEmptyStateComponent(onNewChecklistClicked)
            else -> Unit
        }
    }
}