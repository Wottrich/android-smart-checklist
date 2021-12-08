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
import wottrich.github.io.baseui.ui.Dimens
import wottrich.github.io.database.entity.Task
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
fun TaskListComponent(
    state: TaskListState,
    tasks: List<Task>,
    onAddClicked: (String) -> Unit,
    onUpdateClicked: (Task) -> Unit,
    onDeleteClicked: (Task) -> Unit
) {

    var textFieldValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxHeight()
            .padding(horizontal = Dimens.BaseFour.SizeThree)
    ) {
        AnimatedVisibility(visible = state == TaskListState.Edit) {
            TaskListHeader(
                textFieldValue = textFieldValue,
                onTextFieldValueChange = { textFieldValue = it },
                onAddItem = {
                    onAddClicked(textFieldValue)
                    textFieldValue = ""
                }
            )
        }
        TaskListBody(
            taskList = tasks,
            showDeleteItem = state == TaskListState.Edit,
            onCheckChange = onUpdateClicked,
            onDeleteTask = onDeleteClicked
        )
    }

}