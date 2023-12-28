package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.successEmptyResult
import wottrich.github.io.smartchecklist.data.repository.TaskRepository
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.domain.usecase.ChangeTasksCompletedStatusUseCase.Params

class ChangeTasksCompletedStatusUseCase(private val taskRepository: TaskRepository) :
    KotlinResultUseCase<Params, UseCase.Empty>() {
    override suspend fun execute(params: Params): Result<UseCase.Empty> {
        val tasksToUpdate = params.tasks.map { it.copy(isCompleted = params.isCompleted) }
        taskRepository.updateTasks(tasksToUpdate)
        return successEmptyResult()
    }

    data class Params(val tasks: List<Task>, val isCompleted: Boolean)
}