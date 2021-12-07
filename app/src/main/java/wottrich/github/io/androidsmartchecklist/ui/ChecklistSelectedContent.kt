package wottrich.github.io.androidsmartchecklist.ui

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import wottrich.github.io.androidsmartchecklist.view.HomeState
import wottrich.github.io.androidsmartchecklist.view.HomeViewState
import wottrich.github.io.database.entity.Task
import wottrich.github.io.featurenew.view.screens.checklistdetail.ChecklistScreen
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
    checklistState: HomeState,
    onChangeState: (HomeViewState) -> Unit,
    onAddItemClicked: (String) -> Unit,
    onUpdateItemClicked: (Task) -> Unit,
    onDeleteItemClicked: (Task) -> Unit,
    onConfirmDeleteChecklist: () -> Unit
) {
    if (checklistState.isLoading) {
        CircularProgressIndicator()
    } else {
        val checklistWithTasks = checklistState.checklistWithTasks
        if (checklistWithTasks != null) {
            ChecklistScreen(
                state = checklistState.homeViewState.toTaskListState(),
                checklistWithTasks = checklistWithTasks,
                onChangeState = { onChangeState(it.toHomeViewState()) },
                onAddItemClicked = onAddItemClicked,
                onUpdateItemClicked = onUpdateItemClicked,
                onDeleteItemClicked = onDeleteItemClicked,
                onConfirmDeleteChecklist = onConfirmDeleteChecklist
            )
        } else {
            Text("Empty")
        }
    }
}

private fun HomeViewState.toTaskListState(): TaskListState {
    return when (this) {
        HomeViewState.Delete -> TaskListState.Delete
        HomeViewState.Edit -> TaskListState.Edit
        HomeViewState.Overview -> TaskListState.Overview
    }
}

private fun TaskListState.toHomeViewState(): HomeViewState {
    return when (this) {
        TaskListState.Delete -> HomeViewState.Delete
        TaskListState.Edit -> HomeViewState.Edit
        TaskListState.Overview -> HomeViewState.Overview
    }
}