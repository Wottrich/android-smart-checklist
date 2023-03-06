package wottrich.github.io.impl.presentation.ui

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
import wottrich.github.io.datasource.entity.NewTask

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
    tasks: List<NewTask>,
    onAddClicked: (String) -> Unit,
    onUpdateClicked: (NewTask) -> Unit,
    onDeleteClicked: (NewTask) -> Unit,
    showHeaderComponent: Boolean = true,
    showDeleteIcon: Boolean = true,
) {

    var textFieldValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxHeight()
    ) {
        AnimatedVisibility(visible = showHeaderComponent) {
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
            showDeleteItem = showDeleteIcon,
            onCheckChange = onUpdateClicked,
            onDeleteTask = onDeleteClicked
        )
    }

}