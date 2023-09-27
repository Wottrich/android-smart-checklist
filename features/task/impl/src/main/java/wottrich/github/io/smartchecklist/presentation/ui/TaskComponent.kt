package wottrich.github.io.smartchecklist.presentation.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import wottrich.github.io.smartchecklist.commonuicompose.utils.pressProgressionInteractionState
import wottrich.github.io.smartchecklist.datasource.entity.NewTask

private const val TIME_TO_DELETE_ITEM_IN_MILLIS = 350L

@Composable
fun TaskComponent(
    task: NewTask,
    showDeleteItem: Boolean,
    onCheckChange: () -> Unit,
    onDeleteTask: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val taskItemHeight = remember { mutableStateOf(0) }
    val taskItemWidth = remember { mutableStateOf(0) }
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
                    taskItemHeight.value = it.height
                    taskItemWidth.value = it.width
                }
            )
            TaskDeleteItemLayer(
                width = taskItemWidth.value.toFloat(),
                height = taskItemHeight.value.toFloat(),
                progress = progress
            )
        }
    }
}

