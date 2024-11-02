package wottrich.github.io.smartchecklist.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import wottrich.github.io.smartchecklist.coroutines.FlowableUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.domain.model.ChecklistComponentState
import wottrich.github.io.smartchecklist.domain.model.SortItemType
import wottrich.github.io.smartchecklist.domain.model.TaskComponentModel

class GetTasksSortedUseCase(
    private val observeSortItemSelectedUseCase: ObserveSortItemSelectedUseCase,
    private val observeChecklistWithTasksUseCase: ObserveChecklistWithTasksUseCase,
    private val sortTasksBySelectedSortUseCase: SortTasksBySelectedSortUseCase,
) : FlowableUseCase<UseCase.None, ChecklistComponentState>() {
    override suspend fun execute(params: UseCase.None): Flow<Result<ChecklistComponentState>> {
        return combine(
            observeChecklistWithTasksUseCase(),
            observeSortItemSelectedUseCase()
        ) { resultChecklist, resultSortItem  ->
            val checklist = resultChecklist.getOrNull()
            val sortItem = resultSortItem.getOrNull() ?: SortItemType.UNSELECTED_SORT
            if (checklist != null) {
                Result.success(
                    ChecklistComponentState.Success(
                        TaskComponentModel(
                            checklist = checklist.checklist,
                            tasks = sortTasksBySelectedSortUseCase(
                                SortTasksBySelectedSortUseCase.Params(
                                    selectedSortItem = sortItem,
                                    tasks = checklist.tasks
                                )
                            ).getOrNull().orEmpty(),
                            sectionChecklists = checklist.checklistSectionEmbedded
                        )
                    )
                )
            } else {
                Result.success(ChecklistComponentState.Loading)
            }
        }.distinctUntilChanged()
    }
}