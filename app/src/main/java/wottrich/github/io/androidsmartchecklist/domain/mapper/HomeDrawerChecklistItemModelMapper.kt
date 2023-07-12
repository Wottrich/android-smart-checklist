package wottrich.github.io.androidsmartchecklist.domain.mapper

import wottrich.github.io.androidsmartchecklist.presentation.ui.model.HomeDrawerChecklistItemModel
import wottrich.github.io.datasource.entity.NewChecklistWithNewTasks
import wottrich.github.io.datasource.entity.NewTask

class HomeDrawerChecklistItemModelMapper {
    fun mapToHomeDrawerChecklistItemModelMapper(newChecklistWithNewTasks: NewChecklistWithNewTasks) =
        HomeDrawerChecklistItemModel(
            checklistUuid = newChecklistWithNewTasks.newChecklist.uuid,
            checklistName = newChecklistWithNewTasks.newChecklist.name,
            completeCountLabel = getCompleteCountTasksLabel(newChecklistWithNewTasks.newTasks),
            isChecklistSelected = newChecklistWithNewTasks.newChecklist.isSelected,
            hasUncompletedTask = hasIncompleteTasks(newChecklistWithNewTasks.newTasks)
        )

    private fun getCompleteCountTasksLabel(tasks: List<NewTask>): String {
        val numberOfItems = tasks.size
        val numberOfCompleteItems = tasks.filter { it.isCompleted }.size
        return "$numberOfCompleteItems / $numberOfItems"
    }

    private fun hasIncompleteTasks(tasks: List<NewTask>) = tasks.any { !it.isCompleted }
}