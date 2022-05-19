package wottrich.github.io.impl.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntSize
import wottrich.github.io.baseui.RowComponent
import wottrich.github.io.baseui.TextOneLine
import wottrich.github.io.baseui.icons.DeleteIcon
import wottrich.github.io.datasource.entity.Task
import wottrich.github.io.impl.R.string

@Composable
fun TaskMolecule(
    task: Task,
    showDeleteItem: Boolean,
    onCheckChange: () -> Unit,
    onDeleteTask: () -> Unit,
    interactionSource: MutableInteractionSource,
    onSizeChanged: (IntSize) -> Unit
) {
    RowComponent(
        modifier = Modifier.taskMoleculeModifier(onCheckChange, onSizeChanged),
        leftIconContent = {
            LeftIconContent(
                task = task,
                showDeleteItem = showDeleteItem,
                interactionSource = interactionSource
            )
        },
        leftContent = { LeftContent(task = task) },
        rightIconContent = { RightIconContent(task = task, onCheckChange = onCheckChange) }
    )
}

private fun Modifier.taskMoleculeModifier(
    onCheckChange: () -> Unit,
    onSizeChanged: (IntSize) -> Unit
): Modifier {
    return this
        .clickable { onCheckChange() }
        .clip(TaskItemShape)
        .onSizeChanged(onSizeChanged)
}

@Composable
private fun LeftIconContent(
    task: Task,
    showDeleteItem: Boolean,
    interactionSource: MutableInteractionSource
) {
    AnimatedVisibility(visible = showDeleteItem) {
        DeleteIcon(
            modifier = Modifier
                .clip(CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current
                ) { },
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
    return if (isCompleted) TextDecoration.LineThrough else null
}