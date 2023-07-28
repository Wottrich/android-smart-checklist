package wottrich.github.io.impl.domain.usecase

import wottrich.github.io.coroutines.KotlinResultUseCase
import wottrich.github.io.coroutines.UseCase
import wottrich.github.io.coroutines.base.Result
import wottrich.github.io.coroutines.failureEmptyResult
import wottrich.github.io.coroutines.successEmptyResult
import wottrich.github.io.smartchecklist.datasource.dao.TaskDao
import wottrich.github.io.smartchecklist.datasource.entity.NewTask

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