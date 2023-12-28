package wottrich.github.io.smartchecklist.domain.mapper

import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistWithTasks
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.presentation.ui.model.HomeDrawerChecklistItemModel

class HomeDrawerChecklistItemModelMapper {
    fun mapToHomeDrawerChecklistItemModelMapper(checklistWithNewTasks: ChecklistWithTasks) =
        HomeDrawerChecklistItemModel(
            checklistUuid = checklistWithNewTasks.checklist.uuid,
            checklistName = checklistWithNewTasks.checklist.name,
            completeCountLabel = getCompleteCountTasksLabel(checklistWithNewTasks.tasks),
            isChecklistSelected = checklistWithNewTasks.checklist.isSelected,
            hasUncompletedTask = hasIncompleteTasks(checklistWithNewTasks.tasks)
        )

    private fun getCompleteCountTasksLabel(tasks: List<Task>): String {
        val numberOfItems = tasks.size
        val numberOfCompleteItems = tasks.filter { it.isCompleted }.size
        return "$numberOfCompleteItems / $numberOfItems"
    }

    private fun hasIncompleteTasks(tasks: List<Task>) = tasks.any { !it.isCompleted }
}