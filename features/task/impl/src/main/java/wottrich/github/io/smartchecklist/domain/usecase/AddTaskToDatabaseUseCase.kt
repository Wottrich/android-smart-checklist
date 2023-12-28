package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.data.repository.TaskRepository
import wottrich.github.io.smartchecklist.datasource.data.model.Task

class AddTaskToDatabaseUseCase(private val taskRepository: TaskRepository) :
    KotlinResultUseCase<Task, Task>() {
    override suspend fun execute(params: Task): Result<Task> {
        val id = taskRepository.insertTask(params)
        return if (id != null) Result.success(params)
        else Result.failure(Throwable("Error to add task"))
    }
}