package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.dao.TaskDao
import wottrich.github.io.smartchecklist.datasource.entity.NewTask

class AddTaskToDatabaseUseCase(private val taskDao: TaskDao) :
    KotlinResultUseCase<NewTask, NewTask>() {
    override suspend fun execute(params: NewTask): Result<NewTask> {
        val id = taskDao.insert(params)
        return if (id != null) Result.success(params)
        else Result.failure(Throwable("Error to add task"))
    }

    private fun generateTask(parentUuid: String, taskName: String): NewTask {
        return NewTask(parentUuid = parentUuid, name = taskName)
    }

    data class Params(val parentUuid: String, val taskName: String)
}