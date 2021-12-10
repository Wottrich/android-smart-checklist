package wottrich.github.io.publicandroid.domain.usecase

import wottrich.github.io.database.dao.TaskDao
import wottrich.github.io.database.entity.Task

class GetDeleteTaskUseCase(private val taskDao: TaskDao) {
    suspend operator fun invoke(task: Task) {
        taskDao.delete(task)
    }
}
