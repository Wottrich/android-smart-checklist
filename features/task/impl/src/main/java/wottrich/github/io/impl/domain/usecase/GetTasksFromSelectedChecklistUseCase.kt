package wottrich.github.io.impl.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import wottrich.github.io.datasource.entity.SelectedChecklistTasksSimpleModel
import wottrich.github.io.datasource.repository.ChecklistRepository
import wottrich.github.io.tools.base.FlowableUseCase
import wottrich.github.io.tools.base.Result
import wottrich.github.io.tools.base.UseCase
import wottrich.github.io.tools.base.UseCase.None

class GetTasksFromSelectedChecklistUseCase(
    private val checklistRepository: ChecklistRepository
) : FlowableUseCase<UseCase.None, SelectedChecklistTasksSimpleModel>() {
    override suspend fun execute(params: None): Flow<Result<SelectedChecklistTasksSimpleModel>> {
        return checklistRepository.observeSelectedChecklistsWithTask().mapNotNull {
            Result.success(
                SelectedChecklistTasksSimpleModel(
                    it.newChecklist.uuid,
                    it.newTasks
                )
            )
        }.distinctUntilChanged()
    }
}