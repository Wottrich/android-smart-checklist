package wottrich.github.io.smartchecklist.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import wottrich.github.io.smartchecklist.baseui.RowComponent
import wottrich.github.io.smartchecklist.baseui.StyledText
import wottrich.github.io.smartchecklist.baseui.TextOneLine
import wottrich.github.io.smartchecklist.baseui.icons.DeleteIcon
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.task.R
import wottrich.github.io.smartchecklist.task.R.string

@Composable
fun TaskMolecule(
    task: Task,
    showDeleteItem: Boolean,
    onCheckChange: () -> Unit,
    onDeleteTask: () -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    RowComponent(
        modifier = Modifier.taskMoleculeModifier(onCheckChange),
        leftIconContent = {
            LeftIconContent(
                task = task,
                showDeleteItem = showDeleteItem,
                onDeleteItem = { expanded.value = true }
            )
        },
        leftContent = { LeftContent(task = task) },
        rightIconContent = { RightIconContent(task = task, onCheckChange = onCheckChange) }
    )
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        StyledText(textStyle = MaterialTheme.typography.h6) {
            Text(
                modifier = Modifier.padding(all = Dimens.BaseFour.SizeThree),
                text = stringResource(
                    id = R.string.task_item_component_delete_item_confirm_title,
                    task.name
                )
            )
        }
        DropdownMenuItem(
            onClick = {
                expanded.value = false
                onDeleteTask()
            }
        ) {
            Text(text = stringResource(id = R.string.task_item_component_delete_item_confirm_label))
        }
        DropdownMenuItem(onClick = { expanded.value = false }) {
            Text(text = stringResource(id = R.string.task_item_component_delete_item_cancel_label))
        }
    }
}

private fun Modifier.taskMoleculeModifier(
    onCheckChange: () -> Unit,
): Modifier {
    return this
        .clickable { onCheckChange() }
        .clip(TaskItemShape)
}

@Composable
private fun LeftIconContent(
    task: Task,
    showDeleteItem: Boolean,
    onDeleteItem: () -> Unit
) {
    AnimatedVisibility(visible = showDeleteItem) {
        DeleteIcon(
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = onDeleteItem),
            contentDescription = stringResource(
                id = string.task_item_component_delete_item,
                task.name
            )
        )
    }
}

@Composable
private fun LeftContent(task: Task) {
    TextOneLine(
        primary = {
            Text(
                text = task.name,
                textDecoration = getTextDecoration(task.isCompleted)
            )
        }
    )
}

@Composable
private fun RightIconContent(task: Task, onCheckChange: () -> Unit) {
    IconCompletableTaskContent(
        taskName = task.name,
        isCompletedTask = task.isCompleted
    ) {
        onCheckChange()
    }
}

private fun getTextDecoration(isCompleted: Boolean): TextDecoration? {
    return if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
}

@Preview
@Composable
fun TaskMoleculePreview() {
    TaskMolecule(
        task = Task(
            uuid = "123",
            parentUuid = "123434",
            name = "Task 1",
            isCompleted = false
        ),
        showDeleteItem = true,
        onCheckChange = {},
        onDeleteTask = {},
    )
}