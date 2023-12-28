package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.failureEmptyResult
import wottrich.github.io.smartchecklist.coroutines.successEmptyResult
import wottrich.github.io.smartchecklist.data.repository.TaskRepository
import wottrich.github.io.smartchecklist.datasource.data.model.Task

class AddManyTasksUseCaseImpl(
    private val taskRepository: TaskRepository
) : AddManyTasksUseCase() {
    override suspend fun execute(params: List<Task>): Result<UseCase.Empty> {
        return try {
            taskRepository.insertManyTasks(params)
            successEmptyResult()
        } catch (ex: Exception) {
            failureEmptyResult(ex)
        }
    }
}