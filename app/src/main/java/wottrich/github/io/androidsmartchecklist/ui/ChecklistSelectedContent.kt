package wottrich.github.io.androidsmartchecklist.ui

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import wottrich.github.io.androidsmartchecklist.view.HomeState
import wottrich.github.io.featurenew.view.screens.checklistdetail.ChecklistScreen
import wottrich.github.io.featurenew.view.screens.checklistdetail.TaskListState.Overview

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 01/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@Composable
fun ChecklistSelectedContent(checklistState: HomeState) {
    if (checklistState.isLoading) {
        CircularProgressIndicator()
    } else {
        val checklistWithTasks = checklistState.checklistWithTasks
        if (checklistWithTasks != null) {
            ChecklistScreen(
                state = Overview,
                checklistWithTasks = checklistWithTasks,
                onChangeState = {},
                onAddItemClicked = {},
                onUpdateItemClicked = {},
                onDeleteItemClicked = {},
                onConfirmDeleteChecklist = {}
            )
        } else {
            Text("Empty")
        }
    }
}