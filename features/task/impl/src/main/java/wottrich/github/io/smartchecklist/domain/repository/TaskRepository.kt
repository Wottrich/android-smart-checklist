package wottrich.github.io.smartchecklist.domain.repository

import wottrich.github.io.smartchecklist.datasource.entity.TaskContract

interface TaskRepository {
    suspend fun getTasksFromSelectedChecklist(): List<TaskContract>
}