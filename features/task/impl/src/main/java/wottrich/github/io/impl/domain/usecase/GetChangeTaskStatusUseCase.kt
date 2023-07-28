package wottrich.github.io.impl.domain.usecase

import wottrich.github.io.coroutines.KotlinResultUseCase
import wottrich.github.io.coroutines.UseCase
import wottrich.github.io.coroutines.base.Result
import wottrich.github.io.coroutines.successEmptyResult
import wottrich.github.io.smartchecklist.datasource.dao.TaskDao
import wottrich.github.io.smartchecklist.datasource.entity.NewTask

class GetChangeTaskStatusUseCase(private val taskDao: TaskDao) :
    KotlinResultUseCase<NewTask, UseCase.Empty>() {
    override suspend fun execute(params: NewTask): Result<UseCase.Empty> {
        params.isCompleted = !params.isCompleted
        taskDao.update(params)
        return successEmptyResult()
    }
}
