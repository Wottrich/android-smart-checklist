package wottrich.github.io.impl.domain.usecase

import github.io.wottrich.coroutines.KotlinResultUseCase
import github.io.wottrich.coroutines.UseCase
import github.io.wottrich.coroutines.base.Result
import github.io.wottrich.coroutines.successEmptyResult
import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.NewTask
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