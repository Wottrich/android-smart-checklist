package wottrich.github.io.smartchecklist.domain.repository

import wottrich.github.io.smartchecklist.datasource.data.model.Task

interface TaskRepository {
    suspend fun getTasksByChecklistUuid(checklistUuid: String): List<Task>
    suspend fun insertTask(task: Task): Long?
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
}