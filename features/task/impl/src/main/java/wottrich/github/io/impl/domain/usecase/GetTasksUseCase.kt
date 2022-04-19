package wottrich.github.io.impl.domain.usecase

import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.Task

class GetTasksUseCase(private val taskDao: TaskDao) {
    suspend operator fun invoke(checklistId: String): List<Task> {
        return taskDao.getTasks(checklistId)
    }
}