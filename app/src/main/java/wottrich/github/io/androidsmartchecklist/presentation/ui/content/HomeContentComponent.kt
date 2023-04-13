package wottrich.github.io.androidsmartchecklist.presentation.ui.content

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeUiState
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.impl.presentation.ui.TaskContentComponent

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
    onUpdateItemClicked: (NewTask) -> Unit,
    onError: (Int) -> Unit,
    onNewChecklistClicked: () -> Unit
) {
    when (checklistState.homeUiState) {
        is HomeUiState.Overview -> TaskContentComponent(
            showHeaderComponent = checklistState.isEditUiState,
            showDeleteIcon = checklistState.isEditUiState,
            onUpdateClicked = onUpdateItemClicked,
            onError = onError,
        )
        is HomeUiState.Empty -> HomeEmptyStateComponent(onNewChecklistClicked)
        HomeUiState.Loading -> CircularProgressIndicator()
    }
}