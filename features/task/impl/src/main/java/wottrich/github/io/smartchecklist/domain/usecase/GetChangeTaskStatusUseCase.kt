package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.successEmptyResult
import wottrich.github.io.smartchecklist.data.repository.TaskRepository
import wottrich.github.io.smartchecklist.datasource.data.model.Task

class GetChangeTaskStatusUseCase(private val taskRepository: TaskRepository) :
    KotlinResultUseCase<Task, UseCase.Empty>() {
    override suspend fun execute(params: Task): Result<UseCase.Empty> {
        val taskToUpdate = params.copy(isCompleted = !params.isCompleted)
        taskRepository.updateTask(taskToUpdate)
        return successEmptyResult()
    }
}
