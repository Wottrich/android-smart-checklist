package wottrich.github.io.featurenew.view.screens.checklistdetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import wottrich.github.io.components.ui.Sizes
import wottrich.github.io.featurenew.view.screens.sharedcomponents.TaskListBody
import wottrich.github.io.featurenew.view.screens.sharedcomponents.TaskListHeader

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 21/10/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChecklistDetailScreen(
    viewModel: ChecklistDetailViewModel,
    state: ChecklistDetailState
) {

    var textFieldValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxHeight()
            .padding(horizontal = Sizes.x12)
    ) {
        AnimatedVisibility(visible = state == ChecklistDetailState.Edit) {
            TaskListHeader(
                textFieldValue = textFieldValue,
                onTextFieldValueChange = { textFieldValue = it },
                onAddItem = { viewModel.verifyTaskNameToAddItem(textFieldValue) }
            )
        }
        TaskListBody(
            taskList = viewModel.tasks,
            showDeleteItem = state == ChecklistDetailState.Edit,
            onCheckChange = {
                viewModel.updateTask(it)
            },
            onDeleteTask = {
                viewModel.deleteTask(it)
            }
        )
    }

}