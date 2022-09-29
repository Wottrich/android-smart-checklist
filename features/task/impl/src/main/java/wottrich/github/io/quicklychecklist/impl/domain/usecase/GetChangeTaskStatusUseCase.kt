package wottrich.github.io.quicklychecklist.impl.domain.usecase

import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result
import wottrich.github.io.tools.base.UseCase
import wottrich.github.io.tools.base.UseCase.Empty
import wottrich.github.io.tools.base.successEmptyResult

class GetChangeTaskStatusUseCase(private val taskDao: TaskDao) :
    KotlinResultUseCase<NewTask, UseCase.Empty>() {
    override suspend fun execute(params: NewTask): Result<Empty> {
        params.isCompleted = !params.isCompleted
        taskDao.update(params)
        return successEmptyResult()
    }
}
