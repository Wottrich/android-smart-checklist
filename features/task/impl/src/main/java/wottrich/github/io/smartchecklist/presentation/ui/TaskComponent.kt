package wottrich.github.io.smartchecklist.presentation.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import wottrich.github.io.smartchecklist.baseui.behaviour.progress.pressProgressionInteractionState
import wottrich.github.io.smartchecklist.datasource.data.model.Task

private const val TIME_TO_DELETE_ITEM_IN_MILLIS = 350L

@Composable
fun TaskComponent(
    task: Task,
    showDeleteItem: Boolean,
    onCheckChange: () -> Unit,
    onDeleteTask: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val taskItemHeight = remember { mutableIntStateOf(0) }
    val taskItemWidth = remember { mutableIntStateOf(0) }
    val interactions = interactionSource.interactions
    val progress by pressProgressionInteractionState(
        interactions = interactions,
        timePressingToFinishInMillis = TIME_TO_DELETE_ITEM_IN_MILLIS,
        onFinishTimePressing = { onDeleteTask() }
    )
    TaskRippleLayer(isTaskComplete = task.isCompleted) {
        TaskSurface(isTaskComplete = task.isCompleted) {
            TaskMolecule(
                task = task,
                showDeleteItem = showDeleteItem,
                onCheckChange = onCheckChange,
                interactionSource = interactionSource,
                onSizeChanged = {
                    taskItemHeight.intValue = it.height
                    taskItemWidth.intValue = it.width
                }
            )
            TaskDeleteItemLayer(
                width = taskItemWidth.intValue.toFloat(),
                height = taskItemHeight.intValue.toFloat(),
                progress = progress
            )
        }
    }
}

