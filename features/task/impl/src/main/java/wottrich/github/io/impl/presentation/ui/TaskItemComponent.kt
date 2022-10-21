package wottrich.github.io.impl.presentation.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import github.io.wottrich.common.ui.compose.utils.pressProgressionInteractionState
import wottrich.github.io.datasource.entity.NewTask

private const val TIME_INITIAL_TO_DELETE_ITEM_IN_MILLIS = 0L
private const val TIME_TO_DELETE_ITEM_IN_MILLIS = 500L

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
        initialTimeInMillis = TIME_INITIAL_TO_DELETE_ITEM_IN_MILLIS,
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

