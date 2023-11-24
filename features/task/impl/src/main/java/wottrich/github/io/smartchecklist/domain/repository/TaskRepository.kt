package wottrich.github.io.smartchecklist.domain.repository

import wottrich.github.io.smartchecklist.datasource.entity.NewTask

interface TaskRepository {
    suspend fun getTasksFromSelectedChecklist(): List<NewTask>
}