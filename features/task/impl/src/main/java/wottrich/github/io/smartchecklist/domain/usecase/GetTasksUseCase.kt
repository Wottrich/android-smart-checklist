package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.data.repository.TaskRepository
import wottrich.github.io.smartchecklist.datasource.data.model.Task

class GetTasksUseCase(private val taskRepository: TaskRepository) :
    KotlinResultUseCase<String, List<Task>>() {
    override suspend fun execute(params: String): Result<List<Task>> {
        return try {
            Result.success(taskRepository.getTasksByChecklistUuid(params))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}