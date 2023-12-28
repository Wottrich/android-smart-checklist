package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.checklist.data.repository.ChecklistRepository

class GetTasksFromSelectedChecklistUseCase(
    private val checklistRepository: ChecklistRepository
) : KotlinResultUseCase<String, List<Task>>() {
    override suspend fun execute(params: String): Result<List<Task>> {
        val checklistWithTasks = checklistRepository.getChecklistWithTasksByUuid(params)
        return Result.success(checklistWithTasks.tasks)
    }
}