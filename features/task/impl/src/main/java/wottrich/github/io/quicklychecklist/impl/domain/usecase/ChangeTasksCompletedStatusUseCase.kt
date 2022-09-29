package wottrich.github.io.quicklychecklist.impl.domain.usecase

import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.quicklychecklist.impl.domain.usecase.ChangeTasksCompletedStatusUseCase.Params
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result
import wottrich.github.io.tools.base.UseCase
import wottrich.github.io.tools.base.UseCase.Empty
import wottrich.github.io.tools.base.successEmptyResult

class ChangeTasksCompletedStatusUseCase(private val taskDao: TaskDao) :
    KotlinResultUseCase<Params, UseCase.Empty>() {
    override suspend fun execute(params: Params): Result<Empty> {
        val tasksToUpdate = params.tasks.map { it.copy(isCompleted = params.isCompleted) }
        taskDao.updateTasks(tasksToUpdate)
        return successEmptyResult()
    }

    data class Params(val tasks: List<NewTask>, val isCompleted: Boolean)
}