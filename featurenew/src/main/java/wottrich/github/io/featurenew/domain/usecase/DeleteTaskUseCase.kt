package wottrich.github.io.featurenew.domain.usecase

import wottrich.github.io.database.dao.TaskDao
import wottrich.github.io.database.entity.Task

class DeleteTaskUseCase(private val taskDao: TaskDao) {
    suspend operator fun invoke(task: Task) {
        taskDao.delete(task)
    }
}
