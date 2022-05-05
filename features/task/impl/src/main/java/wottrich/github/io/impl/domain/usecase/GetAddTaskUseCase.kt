package wottrich.github.io.impl.domain.usecase

import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.Task
import wottrich.github.io.impl.domain.usecase.GetAddTaskUseCase.Params
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result

class GetAddTaskUseCase(private val taskDao: TaskDao) : KotlinResultUseCase<Params, Task>() {
    override suspend fun execute(params: Params): Result<Task> {
        val (checklistId, taskName) = params
        val task = generateTask(checklistId, taskName)
        val taskId = taskDao.insert(task)
        task.taskId = taskId
        return Result.success(task)
    }

    private fun generateTask(checklistId: Long, taskName: String): Task {
        return Task(checklistId = checklistId, name = taskName)
    }

    data class Params(val checklistId: Long, val taskName: String)
}