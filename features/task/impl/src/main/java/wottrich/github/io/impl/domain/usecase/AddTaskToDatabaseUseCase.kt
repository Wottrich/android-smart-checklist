package wottrich.github.io.impl.domain.usecase

import github.io.wottrich.coroutines.KotlinResultUseCase
import github.io.wottrich.coroutines.base.Result
import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.impl.domain.usecase.AddTaskToDatabaseUseCase.Params

class AddTaskToDatabaseUseCase(private val taskDao: TaskDao) :
    KotlinResultUseCase<Params, NewTask>() {
    override suspend fun execute(params: Params): Result<NewTask> {
        val (checklistId, taskName) = params
        val task = generateTask(checklistId, taskName)
        val id = taskDao.insert(task)
        return if (id != null) Result.success(task)
        else Result.failure(Throwable("Error to add task"))
    }

    private fun generateTask(parentUuid: String, taskName: String): NewTask {
        return NewTask(parentUuid = parentUuid, name = taskName)
    }

    data class Params(val parentUuid: String, val taskName: String)
}