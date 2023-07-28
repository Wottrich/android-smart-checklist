package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.successEmptyResult
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
