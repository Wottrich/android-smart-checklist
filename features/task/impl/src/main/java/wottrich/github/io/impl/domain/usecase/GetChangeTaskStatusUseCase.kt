package wottrich.github.io.impl.domain.usecase

import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.Task

class GetChangeTaskStatusUseCase(private val taskDao: TaskDao) {
    suspend operator fun invoke(task: Task) {
        task.isCompleted = !task.isCompleted
        taskDao.update(task)
    }
}
