package wottrich.github.io.featurenew.extensions

import androidx.compose.runtime.snapshots.SnapshotStateList
import wottrich.github.io.database.entity.Task

fun SnapshotStateList<Task>.updateLocalTaskList(task: Task): Task {
    val tasks = this
    tasks.find { it.taskId == task.taskId }?.let {
        task.isCompleted = !task.isCompleted
        val indexOfUpdateTask = tasks.indexOf(task)
        tasks[indexOfUpdateTask] = task
    }
    return task
}