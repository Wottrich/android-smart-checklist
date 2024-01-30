package wottrich.github.io.smartchecklist.presentation.ui

import androidx.compose.runtime.Composable
import wottrich.github.io.smartchecklist.datasource.data.model.Task

@Composable
fun TaskComponent(
    task: Task,
    showDeleteItem: Boolean,
    onCheckChange: () -> Unit,
    onDeleteTask: () -> Unit
) {

    TaskRippleLayer(isTaskComplete = task.isCompleted) {
        TaskSurface(isTaskComplete = task.isCompleted) {
            TaskMolecule(
                task = task,
                showDeleteItem = showDeleteItem,
                onCheckChange = onCheckChange,
                onDeleteTask = onDeleteTask
            )
        }
    }
}

