package wottrich.github.io.featurenew.view.screens.sharedcomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import wottrich.github.io.baseui.RowComponent
import wottrich.github.io.baseui.TextOneLine
import wottrich.github.io.baseui.ui.Dimens
import wottrich.github.io.baseui.ui.color.defaultButtonColors
import wottrich.github.io.baseui.ui.color.defaultOutlinedTextFieldColors
import wottrich.github.io.database.entity.Task
import wottrich.github.io.featurenew.R

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 21/10/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@Composable
fun TaskListHeader(
    textFieldValue: String,
    onTextFieldValueChange: ((String) -> Unit),
    onAddItem: (() -> Unit)
) {
    Column {
        TextOneLine(
            modifier = Modifier.padding(vertical = Dimens.BaseFour.SizeTwo),
            primary = {
                Text(text = stringResource(id = R.string.task_list_title))
            }
        )
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = onTextFieldValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = stringResource(id = R.string.task_list_type_task_name_hint))
            },
            colors = defaultOutlinedTextFieldColors()
        )
        Button(
            modifier = Modifier
                .padding(vertical = Dimens.BaseFour.SizeTwo)
                .fillMaxWidth(),
            enabled = textFieldValue.isNotEmpty(),
            onClick = onAddItem,
            colors = defaultButtonColors(),
        ) {
            Text(text = stringResource(id = R.string.task_list_add_item))
        }
    }
}

@Composable
fun TaskListBody(
    taskList: List<Task>,
    onCheckChange: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    showDeleteItem: Boolean = true
) {
    LazyColumn(
        content = {
            items(taskList) { task ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))

                    TaskComponent(
                        task = task,
                        showDeleteItem = showDeleteItem,
                        onCheckChange = { onCheckChange(task) },
                        onDeleteTask = { onDeleteTask(task) }
                    )
                }
            }
        },
        contentPadding = PaddingValues(vertical = Dimens.BaseFour.SizeTwo)
    )
}

@Composable
fun TaskComponent(
    task: Task,
    showDeleteItem: Boolean,
    onCheckChange: () -> Unit,
    onDeleteTask: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(Dimens.BaseFour.SizeTwo),
        elevation = Dimens.BaseFour.SizeOne
    ) {
        RowComponent(
            modifier = Modifier.clickable { onCheckChange() },
            leftContent = {
                TextOneLine(
                    primary = {
                        Text(text = task.name)
                    }
                )
            },
            rightIconContent = {

                IconCompletableTaskContent(
                    taskName = task.name,
                    isCompletedTask = task.isCompleted
                ) {
                    onCheckChange()
                }

                if (showDeleteItem) {
                    Icon(
                        tint = MaterialTheme.colors.onSurface,
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = stringResource(
                            id = R.string.task_delete_item,
                            task.name
                        ),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onDeleteTask() }
                    )
                }
            }
        )
    }
}

@Composable
private fun IconCompletableTaskContent(
    taskName: String,
    isCompletedTask: Boolean,
    onCheckChange: () -> Unit
) {

    val checkIconContentDescription = if (isCompletedTask) {
        R.string.task_click_to_uncheck_item_description
    } else {
        R.string.task_click_to_check_item_description
    }

    val checkIcon = if (isCompletedTask) {
        R.drawable.ic_completed
    } else {
        R.drawable.ic_uncompleted
    }

    Icon(
        tint = MaterialTheme.colors.secondary,
        painter = painterResource(id = checkIcon),
        contentDescription = stringResource(checkIconContentDescription, taskName),
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onCheckChange() }
    )
}