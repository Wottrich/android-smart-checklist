package wottrich.github.io.quicklychecklist.impl.domain.usecase

import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result

class GetTasksUseCase(private val taskDao: TaskDao) : KotlinResultUseCase<String, List<NewTask>>() {
    override suspend fun execute(params: String): Result<List<NewTask>> {
        return try {
            Result.success(taskDao.getTasks(params))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}