package wottrich.github.io.impl.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.FlowableUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import wottrich.github.io.smartchecklist.datasource.entity.SelectedChecklistTasksSimpleModel
import wottrich.github.io.smartchecklist.datasource.repository.ChecklistRepository

class GetTasksFromSelectedChecklistUseCase(
    private val checklistRepository: ChecklistRepository
) : FlowableUseCase<UseCase.None, SelectedChecklistTasksSimpleModel>() {
    override suspend fun execute(params: UseCase.None): Flow<Result<SelectedChecklistTasksSimpleModel>> {
        return checklistRepository.observeSelectedChecklistWithTasks().mapNotNull {
            if (it != null) {
                Result.success(
                    SelectedChecklistTasksSimpleModel(
                        it.newChecklist.uuid,
                        it.newTasks
                    )
                )
            } else null
        }.distinctUntilChanged()
    }
}