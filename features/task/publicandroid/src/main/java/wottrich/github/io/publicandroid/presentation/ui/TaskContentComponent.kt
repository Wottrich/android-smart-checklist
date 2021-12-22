package wottrich.github.io.publicandroid.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import wottrich.github.io.database.entity.Task

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
fun TaskContentComponent(
    tasks: List<Task>,
    onAddClicked: (String) -> Unit,
    onUpdateClicked: (Task) -> Unit,
    onDeleteClicked: (Task) -> Unit,
    isEditMode: Boolean = true,
) {

    var textFieldValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxHeight()
    ) {
        AnimatedVisibility(visible = isEditMode) {
            TaskHeaderComponent(
                textFieldValue = textFieldValue,
                onTextFieldValueChange = { textFieldValue = it },
                onAddItem = {
                    onAddClicked(textFieldValue)
                    textFieldValue = ""
                }
            )
        }
        TaskLazyColumnComponent(
            taskList = tasks,
            showDeleteItem = isEditMode,
            onCheckChange = onUpdateClicked,
            onDeleteTask = onDeleteClicked
        )
    }

}