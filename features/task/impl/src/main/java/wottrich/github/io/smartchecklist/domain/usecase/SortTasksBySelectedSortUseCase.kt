package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.domain.model.SortItemType
import wottrich.github.io.smartchecklist.presentation.task.model.BaseTaskListItem
import wottrich.github.io.smartchecklist.task.R

class SortTasksBySelectedSortUseCase :
    KotlinResultUseCase<SortTasksBySelectedSortUseCase.Params, List<BaseTaskListItem>>() {
    override suspend fun execute(params: Params): Result<List<BaseTaskListItem>> {
        val selectedSort = params.selectedSortItem
        return Result.success(getSortedListBySelectedType(params.tasks, selectedSort))
    }

    private fun getSortedListBySelectedType(
        tasks: List<Task>,
        selectedSort: SortItemType
    ): List<BaseTaskListItem> {
        val taskItems = tasks.map { BaseTaskListItem.TaskItem(it) }
        val tasksGroupedByIsCompleted =
            taskItems.groupBy { it.getTaskItemOrNull()?.task?.isCompleted }
        val completedTasks = tasksGroupedByIsCompleted[true].orEmpty()
        val uncompletedTasks = tasksGroupedByIsCompleted[false].orEmpty()
        return when (selectedSort) {
            SortItemType.COMPLETED_TASKS -> {
                completedTasks + BaseTaskListItem.SectionItem(R.string.task_sort_uncompleted_task_item) + uncompletedTasks
            }

            SortItemType.UNCOMPLETED_TASKS -> {
                uncompletedTasks + BaseTaskListItem.SectionItem(R.string.task_sort_completed_task_item) + completedTasks
            }

            SortItemType.UNSELECTED_SORT -> taskItems
        }
    }

    class Params(
        val selectedSortItem: SortItemType,
        val tasks: List<Task>
    )
}