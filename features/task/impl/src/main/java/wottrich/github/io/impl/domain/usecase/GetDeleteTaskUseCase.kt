package wottrich.github.io.impl.domain.usecase

import github.io.wottrich.coroutines.KotlinResultUseCase
import github.io.wottrich.coroutines.UseCase
import github.io.wottrich.coroutines.base.Result
import github.io.wottrich.coroutines.successEmptyResult
import wottrich.github.io.smartchecklist.datasource.dao.TaskDao
import wottrich.github.io.smartchecklist.datasource.entity.NewTask

class GetDeleteTaskUseCase(private val taskDao: TaskDao) :
    KotlinResultUseCase<NewTask, UseCase.Empty>() {
    override suspend fun execute(params: NewTask): Result<UseCase.Empty> {
        taskDao.delete(params)
        return successEmptyResult()
    }
}
