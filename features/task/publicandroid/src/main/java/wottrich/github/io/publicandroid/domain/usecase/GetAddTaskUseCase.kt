package wottrich.github.io.publicandroid.domain.usecase

import wottrich.github.io.database.dao.TaskDao
import wottrich.github.io.database.entity.Task

class GetAddTaskUseCase(private val taskDao: TaskDao) {

    suspend operator fun invoke(checklistId: Long, taskName: String): Task {
        val task = generateTask(checklistId, taskName)
        val taskId = taskDao.insert(task)
        task.taskId = taskId
        return task
    }

    private fun generateTask(checklistId: Long, taskName: String): Task {
        return Task(checklistId = checklistId, name = taskName)
    }

}