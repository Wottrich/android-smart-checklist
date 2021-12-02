package wottrich.github.io.featurenew.view.screens.checklistdetail

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import wottrich.github.io.baseui.TopBarContent
import wottrich.github.io.database.entity.ChecklistWithTasks
import wottrich.github.io.database.entity.Task
import wottrich.github.io.featurenew.R.string

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 30/11/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@Composable
fun ChecklistScreen(
    state: TaskListState,
    checklistWithTasks: ChecklistWithTasks,
    onChangeState: (TaskListState) -> Unit,
    onAddItemClicked: (String) -> Unit,
    onUpdateItemClicked: (Task) -> Unit,
    onDeleteItemClicked: (Task) -> Unit,
    onConfirmDeleteChecklist: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val tasks = checklistWithTasks.tasks
    Scaffold(
        topBar = {
            TopBarContent(
                title = {},
                actionsContent = {
                    TopBarActionsContent(
                        state = state,
                        onShowDeleteConfirmDialog = { showDeleteDialog = true },
                        onChangeState = onChangeState
                    )
                }
            )
        }
    ) {
        TaskListComponent(
            state = state,
            tasks = tasks,
            onAddClicked = onAddItemClicked,
            onUpdateClicked = onUpdateItemClicked,
            onDeleteClicked = onDeleteItemClicked
        )
    }
    DeleteAlertDialogContent(
        showDeleteDialog = showDeleteDialog,
        onConfirmDeleteChecklist = onConfirmDeleteChecklist,
        onDismiss = {
            showDeleteDialog = false
        }
    )
}

@Composable
private fun TopBarActionsContent(
    state: TaskListState,
    onShowDeleteConfirmDialog: (() -> Unit),
    onChangeState: (TaskListState) -> Unit
) {
    IconButton(onClick = onShowDeleteConfirmDialog) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(
                id = string.checklist_delete_checklist_content_description
            )
        )
    }
    EditIconStateContent(state = state, onChangeState = onChangeState)
}

@Composable
private fun EditIconStateContent(
    state: TaskListState,
    onChangeState: (TaskListState) -> Unit
) {
    when (state) {
        TaskListState.Edit -> {
            IconButton(
                onClick = {
                    onChangeState(TaskListState.Overview)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(
                        id = string.checklist_finish_edit_content_description
                    )
                )
            }
        }
        TaskListState.Overview -> {
            IconButton(
                onClick = {
                    onChangeState(TaskListState.Edit)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(
                        id = string.checklist_edit_checklist_content_description
                    )
                )
            }
        }
        else -> Unit
    }
}

@Composable
private fun DeleteAlertDialogContent(
    showDeleteDialog: Boolean,
    onConfirmDeleteChecklist: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(stringResource(id = string.attention)) },
            text = { Text(text = stringResource(id = string.checklist_delete_checklist_confirm_label)) },
            confirmButton = {
                Button(onClick = { onConfirmDeleteChecklist() }) {
                    Text(text = stringResource(id = string.yes))
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text(text = stringResource(id = string.no))
                }
            }
        )
    }
}