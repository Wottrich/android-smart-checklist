package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.entity.NewTask
import wottrich.github.io.smartchecklist.domain.repository.TaskRepository

class GetTasksUseCase(
    private val taskRepository: TaskRepository
) : KotlinResultUseCase<UseCase.None, List<NewTask>>() {
    override suspend fun execute(params: UseCase.None): Result<List<NewTask>> {
        return try {
            Result.success(taskRepository.getTasksFromSelectedChecklist())
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}