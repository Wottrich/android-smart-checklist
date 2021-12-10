package wottrich.github.io.androidsmartchecklist.presentation.ui.content

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import wottrich.github.io.androidsmartchecklist.presentation.ui.HomeEmptyStateComponent
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeViewState
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
    if (checklistState.homeViewState == HomeViewState.Loading) {
        CircularProgressIndicator()
    } else {
        val checklistWithTasks = checklistState.checklistWithTasks
        if (checklistWithTasks != null) {
            TaskContentComponent(
                tasks = tasks,
                isEditMode = checklistState.homeViewState == HomeViewState.Edit,
                onAddClicked = onAddItemClicked,
                onUpdateClicked = onUpdateItemClicked,
                onDeleteClicked = onDeleteItemClicked
            )
        } else {
            HomeEmptyStateComponent(onNewChecklistClicked)
        }
    }
}