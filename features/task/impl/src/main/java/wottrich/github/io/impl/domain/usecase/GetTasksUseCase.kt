package wottrich.github.io.impl.domain.usecase

import github.io.wottrich.coroutines.KotlinResultUseCase
import github.io.wottrich.coroutines.base.Result
import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.NewTask

class GetTasksUseCase(private val taskDao: TaskDao) : KotlinResultUseCase<String, List<NewTask>>() {
    override suspend fun execute(params: String): Result<List<NewTask>> {
        return try {
            Result.success(taskDao.getTasks(params))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}