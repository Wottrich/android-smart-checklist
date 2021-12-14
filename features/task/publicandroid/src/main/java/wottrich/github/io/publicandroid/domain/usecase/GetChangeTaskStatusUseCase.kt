package wottrich.github.io.publicandroid.domain.usecase

import wottrich.github.io.database.dao.TaskDao
import wottrich.github.io.database.entity.Task

class GetChangeTaskStatusUseCase(private val taskDao: TaskDao) {
    suspend operator fun invoke(task: Task) {
        task.isCompleted = !task.isCompleted
        taskDao.update(task)
    }
}
