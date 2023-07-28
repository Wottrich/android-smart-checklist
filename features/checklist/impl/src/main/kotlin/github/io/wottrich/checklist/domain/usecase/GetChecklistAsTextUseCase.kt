package github.io.wottrich.checklist.domain.usecase

import github.io.wottrich.checklist.domain.GetChecklistAsTextUseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.repository.ChecklistRepository

class GetChecklistAsTextUseCaseImpl(
    private val checklistRepository: ChecklistRepository
) : GetChecklistAsTextUseCase() {
    override suspend fun execute(params: String): Result<String> {
        return try {
            Result.success(checklistRepository.getChecklistWithTasksByUuid(params).toString())
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}