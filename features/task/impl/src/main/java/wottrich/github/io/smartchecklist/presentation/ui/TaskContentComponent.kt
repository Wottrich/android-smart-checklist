package wottrich.github.io.smartchecklist.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.domain.model.TaskComponentModel
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction
import wottrich.github.io.smartchecklist.presentation.state.TaskComponentUiState
import wottrich.github.io.smartchecklist.presentation.ui.checklistinformationheader.ChecklistInformationHeaderComponent
import wottrich.github.io.smartchecklist.presentation.viewmodel.TaskComponentViewModel
import wottrich.github.io.smartchecklist.presentation.viewmodel.TaskComponentViewModelUiEffect

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 21/10/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@Composable
fun TaskContentComponent(
    showHeaderComponent: Boolean = true,
    showDeleteIcon: Boolean = true,
    onUpdateClicked: (Task) -> Unit,
    onError: (stringRes: Int) -> Unit,
    onTaskCounterClicked: () -> Unit,
    viewModel: TaskComponentViewModel = getViewModel()
) {

    val state by viewModel.uiState.collectAsState()
    val effects = viewModel.uiEffect

    Effects(
        effects = effects,
        onError = onError
    )

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxHeight()
    ) {
        TaskEditHeaderVisibility(
            showHeaderComponent = showHeaderComponent,
            taskName = state.taskName,
            showSectionButton = state.showSectionButton,
            onTextFieldValueChange = {
                viewModel.sendAction(TaskComponentViewModelAction.Action.OnTextChanged(it))
            },
            onAddItem = {
                viewModel.sendAction(TaskComponentViewModelAction.Action.AddTask)
            },
            onAddSection = {
                viewModel.sendAction(TaskComponentViewModelAction.Action.AddSection)
            }
        )
        Divider()
        AnimatedVisibility(visible = !showHeaderComponent) {
            ChecklistInformationHeaderComponent(onTaskCounterClicked = onTaskCounterClicked)
        }
        Divider()
        state.checklist?.let {
            TaskList(
                tasks = it,
                showDeleteIcon = showDeleteIcon,
                onCheckChange = {
                    viewModel.sendAction(
                        TaskComponentViewModelAction.Action.ChangeTaskStatus(it)
                    )
                    onUpdateClicked(it)
                },
                onDeleteTask = {
                    viewModel.sendAction(TaskComponentViewModelAction.Action.DeleteTask(it))
                }
            )
        }
    }
}

@Composable
private fun Effects(
    effects: Flow<TaskComponentViewModelUiEffect>,
    onError: (stringRes: Int) -> Unit
) {
    LaunchedEffect(key1 = effects) {
        effects.collect {
            when (it) {
                is TaskComponentViewModelUiEffect.OnError -> onError(it.stringRes)
            }
        }
    }
}

@Composable
private fun Screen(
    showHeaderComponent: Boolean,
    state: TaskComponentUiState,
    onTextFieldValueChange: (String) -> Unit,
    onAddItem: () -> Unit,
    onAddSection: () -> Unit,
    onTaskCounterClicked: () -> Unit,
) {

}

@Composable
private fun ColumnScope.TaskEditHeaderVisibility(
    showHeaderComponent: Boolean,
    taskName: String,
    showSectionButton: Boolean,
    onTextFieldValueChange: (String) -> Unit,
    onAddItem: () -> Unit,
    onAddSection: () -> Unit,
) {
    AnimatedVisibility(visible = showHeaderComponent) {
        TaskEditHeaderComponent(
            textFieldValue = taskName,
            showSectionButton = showSectionButton,
            onTextFieldValueChange = onTextFieldValueChange,
            onAddItem = onAddItem,
            onAddSection = onAddSection
        )
    }
}

@Composable
private fun TaskList(
    tasks: TaskComponentModel,
    showDeleteIcon: Boolean,
    onCheckChange: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    TaskLazyColumnComponent(
        taskList = tasks,
        showDeleteItem = showDeleteIcon,
        onCheckChange = onCheckChange,
        onDeleteTask = onDeleteTask
    )
}