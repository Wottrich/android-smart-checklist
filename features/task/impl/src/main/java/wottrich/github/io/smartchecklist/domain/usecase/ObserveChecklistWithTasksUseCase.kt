package wottrich.github.io.smartchecklist.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import wottrich.github.io.smartchecklist.checklist.data.repository.ChecklistRepository
import wottrich.github.io.smartchecklist.coroutines.FlowableUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistWithTasks

class ObserveChecklistWithTasksUseCase(
    private val checklistRepository: ChecklistRepository
) : FlowableUseCase<UseCase.None, ChecklistWithTasks>() {
    override suspend fun execute(params: UseCase.None): Flow<Result<ChecklistWithTasks>> {
        return checklistRepository.observeSelectedChecklistWithTasks().map {
            if (it != null) Result.success(it)
            else Result.failure(NullPointerException())
        }
    }
}