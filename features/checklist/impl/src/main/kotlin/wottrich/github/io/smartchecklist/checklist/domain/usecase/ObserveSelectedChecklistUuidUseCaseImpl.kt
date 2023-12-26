package wottrich.github.io.smartchecklist.checklist.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import wottrich.github.io.smartchecklist.checklist.domain.ObserveSelectedChecklistUuidUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.repository.ChecklistRepository
import java.io.InvalidObjectException

class ObserveSelectedChecklistUuidUseCaseImpl(
    private val checklistRepository: ChecklistRepository
) : ObserveSelectedChecklistUuidUseCase() {
    override suspend fun execute(params: UseCase.None): Flow<Result<String>> {
        return checklistRepository.observeSelectedChecklistUuid().mapNotNull {
            if (it.isNullOrBlank()) throw InvalidObjectException("Uuid null means no selected checklist")
            else Result.success(it)
        }
    }
}