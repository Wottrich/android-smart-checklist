package wottrich.github.io.smartchecklist.newchecklist.domain.usecase

import wottrich.github.io.smartchecklist.checklist.data.repository.ChecklistRepository
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.newchecklist.domain.model.NewChecklistModel

class AddNewChecklistUseCaseImpl(
    private val checklistRepository: ChecklistRepository
) : AddNewChecklistUseCase() {
    override suspend fun execute(params: NewChecklistModel): Result<Long?> {
        return try {
            val checklist = Checklist(uuid = params.uuid, name = params.name)
            Result.success(checklistRepository.insertChecklist(checklist))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}