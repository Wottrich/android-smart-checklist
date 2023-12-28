package wottrich.github.io.smartchecklist.newchecklist.domain

import wottrich.github.io.smartchecklist.checklist.data.repository.ChecklistRepository
import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist

class AddNewChecklistUseCase(
    private val checklistRepository: ChecklistRepository
) : KotlinResultUseCase<Checklist, Long?>() {
    override suspend fun execute(params: Checklist): Result<Long?> {
        return try {
            Result.success(checklistRepository.insertChecklist(params))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}