package wottrich.github.io.impl.domain.usecase

import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.Task
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result

class GetTasksUseCase(private val taskDao: TaskDao) : KotlinResultUseCase<String, List<Task>>() {
    override suspend fun execute(params: String): Result<List<Task>> {
        val checklistId = params
        return try {
            Result.success(taskDao.getTasks(checklistId))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}