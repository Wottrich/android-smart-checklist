package wottrich.github.io.smartchecklist.checklist.domain.usecase

import wottrich.github.io.smartchecklist.checklist.data.repository.ChecklistRepository
import wottrich.github.io.smartchecklist.checklist.domain.GetSelectedChecklistUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist

class GetSelectedChecklistUseCaseImpl(
    private val checklistRepository: ChecklistRepository
) : GetSelectedChecklistUseCase() {
    override suspend fun execute(params: UseCase.None): Result<Checklist?> {
        return try {
            Result.success(checklistRepository.getSelectedChecklist())
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}