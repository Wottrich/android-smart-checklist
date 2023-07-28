package wottrich.github.io.impl.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.dao.TaskDao
import wottrich.github.io.smartchecklist.datasource.entity.NewTask

class GetTasksUseCase(private val taskDao: TaskDao) : KotlinResultUseCase<String, List<NewTask>>() {
    override suspend fun execute(params: String): Result<List<NewTask>> {
        return try {
            Result.success(taskDao.getTasks(params))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}