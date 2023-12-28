package wottrich.github.io.smartchecklist.newchecklist.domain.usecase

import wottrich.github.io.smartchecklist.checklist.data.repository.ChecklistRepository
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist

class AddNewChecklistUseCaseImpl(
    private val checklistRepository: ChecklistRepository
) : AddNewChecklistUseCase() {
    override suspend fun execute(params: Checklist): Result<Long?> {
        return try {
            Result.success(checklistRepository.insertChecklist(params))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}