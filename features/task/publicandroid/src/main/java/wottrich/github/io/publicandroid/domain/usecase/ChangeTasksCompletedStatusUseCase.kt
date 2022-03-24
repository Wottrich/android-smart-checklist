package wottrich.github.io.publicandroid.domain.usecase

import wottrich.github.io.database.dao.TaskDao
import wottrich.github.io.database.entity.Task

class ChangeTasksCompletedStatusUseCase(private val taskDao: TaskDao) {
    suspend operator fun invoke(tasks: List<Task>, isCompleted: Boolean) {
        val tasksToUpdate = tasks.map { it.copy(isCompleted = isCompleted) }
        taskDao.updateTasks(tasksToUpdate)
    }
}