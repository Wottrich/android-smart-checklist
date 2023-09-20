package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.entity.NewTask
import wottrich.github.io.smartchecklist.domain.model.SortItemType
import wottrich.github.io.smartchecklist.presentation.sort.model.TaskSortItemState

class SortTasksBySelectedSortUseCase : KotlinResultUseCase<SortTasksBySelectedSortUseCase.Params, List<NewTask>>() {
    override suspend fun execute(params: Params): Result<List<NewTask>> {
        val selectedSort = getSelectedSortItem(params.sortItems)
        return if (selectedSort != SortItemType.UNSELECTED_SORT) {
            val sortedTasks = params.tasks.sortedBy {
                when (selectedSort) {
                    SortItemType.COMPLETED_TASKS -> it.isCompleted
                    SortItemType.UNCOMPLETED_TASKS -> !it.isCompleted
                    else -> true
                }
            }
            Result.success(sortedTasks)
        } else Result.success(params.tasks)
    }

    private fun getSelectedSortItem(sortItems: List<TaskSortItemState>): SortItemType {
        return sortItems.firstOrNull { it.isSelected }?.type ?: SortItemType.UNSELECTED_SORT
    }

    class Params(
        val sortItems: List<TaskSortItemState>,
        val tasks: List<NewTask>
    )
}