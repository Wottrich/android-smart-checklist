package wottrich.github.io.impl.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.impl.presentation.action.TaskComponentViewModelAction
import wottrich.github.io.impl.presentation.viewmodel.TaskComponentViewModel
import wottrich.github.io.impl.presentation.viewmodel.TaskComponentViewModelUiEffect

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
    onUpdateClicked: (NewTask) -> Unit,
    onError: (stringRes: Int) -> Unit,
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
        Header(
            showHeaderComponent = showHeaderComponent,
            taskName = state.taskName,
            onTextFieldValueChange = {
                viewModel.sendAction(TaskComponentViewModelAction.Action.OnTextChanged(it))
            },
            onAddItem = {
                viewModel.sendAction(TaskComponentViewModelAction.Action.AddTask)
            }
        )
        TaskList(
            tasks = viewModel.tasks,
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
private fun ColumnScope.Header(
    showHeaderComponent: Boolean,
    taskName: String,
    onTextFieldValueChange: (String) -> Unit,
    onAddItem: () -> Unit,
) {
    AnimatedVisibility(visible = showHeaderComponent) {
        TaskHeaderComponent(
            textFieldValue = taskName,
            onTextFieldValueChange = onTextFieldValueChange,
            onAddItem = onAddItem
        )
    }
}

@Composable
private fun TaskList(
    tasks: List<NewTask>,
    showDeleteIcon: Boolean,
    onCheckChange: (NewTask) -> Unit,
    onDeleteTask: (NewTask) -> Unit
) {
    TaskLazyColumnComponent(
        taskList = tasks,
        showDeleteItem = showDeleteIcon,
        onCheckChange = onCheckChange,
        onDeleteTask = onDeleteTask
    )
}