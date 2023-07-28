package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.coroutines.FlowableUseCase
import wottrich.github.io.coroutines.UseCase
import wottrich.github.io.coroutines.base.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import wottrich.github.io.smartchecklist.domain.mapper.HomeDrawerChecklistItemModelMapper
import wottrich.github.io.smartchecklist.presentation.ui.model.HomeDrawerChecklistItemModel
import wottrich.github.io.smartchecklist.datasource.repository.ChecklistRepository

class GetChecklistDrawerUseCase(
    private val checklistRepository: ChecklistRepository,
    private val mapper: HomeDrawerChecklistItemModelMapper
) : FlowableUseCase<UseCase.None, List<HomeDrawerChecklistItemModel>>() {
    override suspend fun execute(params: UseCase.None): Flow<Result<List<HomeDrawerChecklistItemModel>>> {
        return checklistRepository.observeAllChecklistsWithTask().mapNotNull {
            val checklists = it.map { newChecklistWithNewTasks ->
                mapper.mapToHomeDrawerChecklistItemModelMapper(newChecklistWithNewTasks)
            }
            Result.success(checklists)
        }.distinctUntilChanged()
    }
}