package wottrich.github.io.impl.domain.usecase

import github.io.wottrich.coroutines.FlowableUseCase
import github.io.wottrich.coroutines.UseCase
import github.io.wottrich.coroutines.base.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import wottrich.github.io.datasource.entity.SelectedChecklistTasksSimpleModel
import wottrich.github.io.datasource.repository.ChecklistRepository

class GetTasksFromSelectedChecklistUseCase(
    private val checklistRepository: ChecklistRepository
) : FlowableUseCase<UseCase.None, SelectedChecklistTasksSimpleModel>() {
    override suspend fun execute(params: UseCase.None): Flow<Result<SelectedChecklistTasksSimpleModel>> {
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