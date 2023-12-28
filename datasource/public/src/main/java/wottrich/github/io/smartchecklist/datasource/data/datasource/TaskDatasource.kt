package wottrich.github.io.smartchecklist.datasource.data.datasource

import wottrich.github.io.smartchecklist.datasource.data.model.Task

interface TaskDatasource {
    suspend fun getTasksByChecklistUuid(checklistUuid: String): List<Task>
    suspend fun insertTask(task: Task): Long?
    suspend fun insertManyTasks(tasks: List<Task>)
    suspend fun updateTask(task: Task)
    suspend fun updateTasks(tasks: List<Task>)
    suspend fun deleteTask(task: Task)
}