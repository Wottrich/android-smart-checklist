package wottrich.github.io.featurenew.domain.usecase

import wottrich.github.io.database.dao.TaskDao
import wottrich.github.io.database.entity.Task

class UpdateTaskUseCase(private val taskDao: TaskDao) {
    suspend operator fun invoke(task: Task) {
        taskDao.update(task)
    }
}
