package wottrich.github.io.impl.domain.usecase

import github.io.wottrich.coroutines.KotlinResultUseCase
import github.io.wottrich.coroutines.UseCase
import github.io.wottrich.coroutines.base.Result
import github.io.wottrich.coroutines.failureEmptyResult
import github.io.wottrich.coroutines.successEmptyResult
import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.NewTask

class AddManyTasksUseCase(
    private val taskDao: TaskDao
) : KotlinResultUseCase<List<NewTask>, UseCase.Empty>() {
    override suspend fun execute(params: List<NewTask>): Result<UseCase.Empty> {
        return try {
            taskDao.insertMany(params)
            successEmptyResult()
        } catch (ex: Exception) {
            failureEmptyResult(ex)
        }
    }
}