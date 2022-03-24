package wottrich.github.io.publicandroid.domain.usecase

import wottrich.github.io.database.dao.TaskDao
import wottrich.github.io.database.entity.Task

class GetTasksUseCase(private val taskDao: TaskDao) {
    suspend operator fun invoke(checklistId: String): List<Task> {
        return taskDao.getTasks(checklistId)
    }
}