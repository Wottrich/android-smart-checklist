package wottrich.github.io.androidsmartchecklist.ui

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import wottrich.github.io.androidsmartchecklist.view.HomeState
import wottrich.github.io.androidsmartchecklist.view.HomeViewState
import wottrich.github.io.database.entity.Task
import wottrich.github.io.featurenew.view.screens.checklistdetail.TaskListComponent
import wottrich.github.io.featurenew.view.screens.checklistdetail.TaskListState

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 01/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@Composable
fun ChecklistSelectedContent(
    tasks: List<Task>,
    checklistState: HomeState,
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
            TaskListComponent(
                state = checklistState.homeViewState.toTaskListState(),
                tasks = tasks,
                onAddClicked = onAddItemClicked,
                onUpdateClicked = onUpdateItemClicked,
                onDeleteClicked = onDeleteItemClicked
            )
        } else {
            ChecklistEmptyState(onNewChecklistClicked)
        }
    }
}

private fun HomeViewState.toTaskListState(): TaskListState {
    return when (this) {
        HomeViewState.Edit -> TaskListState.Edit
        else -> TaskListState.Overview
    }
}