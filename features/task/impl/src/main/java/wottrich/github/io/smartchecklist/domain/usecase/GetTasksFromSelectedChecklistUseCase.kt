package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.entity.NewTask
import wottrich.github.io.smartchecklist.datasource.repository.ChecklistRepository

class GetTasksFromSelectedChecklistUseCase(
    private val checklistRepository: ChecklistRepository
) : KotlinResultUseCase<String, List<NewTask>>() {
    override suspend fun execute(params: String): Result<List<NewTask>> {
        val checklistWithTasks = checklistRepository.getChecklistWithTasksByUuid(params)
        return Result.success(checklistWithTasks.newTasks)
    }
}