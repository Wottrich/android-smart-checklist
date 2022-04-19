package wottrich.github.io.impl.domain.usecase

import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.Task

class ChangeTasksCompletedStatusUseCase(private val taskDao: TaskDao) {
    suspend operator fun invoke(tasks: List<Task>, isCompleted: Boolean) {
        val tasksToUpdate = tasks.map { it.copy(isCompleted = isCompleted) }
        taskDao.updateTasks(tasksToUpdate)
    }
}