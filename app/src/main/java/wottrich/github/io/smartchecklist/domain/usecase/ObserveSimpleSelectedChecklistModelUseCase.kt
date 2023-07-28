package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.FlowableUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import wottrich.github.io.smartchecklist.domain.mapper.SimpleChecklistModelMapper
import wottrich.github.io.smartchecklist.presentation.ui.model.SimpleChecklistModel
import wottrich.github.io.smartchecklist.datasource.repository.ChecklistRepository

class ObserveSimpleSelectedChecklistModelUseCase(
    private val checklistRepository: ChecklistRepository,
    private val mapper: SimpleChecklistModelMapper
) : FlowableUseCase<UseCase.None, SimpleChecklistModel>() {
    override suspend fun execute(params: UseCase.None): Flow<Result<SimpleChecklistModel>> {
        return checklistRepository.observeSelectedChecklistWithTasks().map {
            if (it != null) Result.success(mapper.mapToSimpleChecklistModel(it.newChecklist))
            else Result.failure(NullPointerException("Checklist is null"))
        }.distinctUntilChanged()
    }
}