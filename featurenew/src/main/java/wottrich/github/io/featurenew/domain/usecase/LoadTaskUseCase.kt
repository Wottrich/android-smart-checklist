package wottrich.github.io.featurenew.domain.usecase

import wottrich.github.io.database.dao.TaskDao
import wottrich.github.io.database.entity.Task

class LoadTaskUseCase(private val taskDao: TaskDao) {
    suspend operator fun invoke(checklistId: String): List<Task> {
        return taskDao.getTasks(checklistId)
    }
}