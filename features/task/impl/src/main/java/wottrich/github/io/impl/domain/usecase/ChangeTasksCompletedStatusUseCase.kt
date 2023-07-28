package wottrich.github.io.impl.domain.usecase

import wottrich.github.io.coroutines.KotlinResultUseCase
import wottrich.github.io.coroutines.UseCase
import wottrich.github.io.coroutines.base.Result
import wottrich.github.io.coroutines.successEmptyResult
import wottrich.github.io.smartchecklist.datasource.dao.TaskDao
import wottrich.github.io.smartchecklist.datasource.entity.NewTask
import wottrich.github.io.impl.domain.usecase.ChangeTasksCompletedStatusUseCase.Params

class ChangeTasksCompletedStatusUseCase(private val taskDao: TaskDao) :
    KotlinResultUseCase<Params, UseCase.Empty>() {
    override suspend fun execute(params: Params): Result<UseCase.Empty> {
        val tasksToUpdate = params.tasks.map { it.copy(isCompleted = params.isCompleted) }
        taskDao.updateTasks(tasksToUpdate)
        return successEmptyResult()
    }

    data class Params(val tasks: List<NewTask>, val isCompleted: Boolean)
}