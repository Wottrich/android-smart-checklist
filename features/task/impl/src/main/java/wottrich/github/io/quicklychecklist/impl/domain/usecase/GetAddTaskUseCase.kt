package wottrich.github.io.quicklychecklist.impl.domain.usecase

import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.quicklychecklist.impl.domain.usecase.GetAddTaskUseCase.Params
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result

class GetAddTaskUseCase(private val taskDao: TaskDao) : KotlinResultUseCase<Params, NewTask>() {
    override suspend fun execute(params: Params): Result<NewTask> {
        val (checklistId, taskName) = params
        val task = generateTask(checklistId, taskName)
        taskDao.insert(task)
        return Result.success(task)
    }

    private fun generateTask(parentUuid: String, taskName: String): NewTask {
        return NewTask(parentUuid = parentUuid, name = taskName)
    }

    data class Params(val parentUuid: String, val taskName: String)
}