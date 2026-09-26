package wottrich.github.io.smartchecklist.domain.model

import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistSectionWithTask
import wottrich.github.io.smartchecklist.presentation.task.model.BaseTaskListItem

data class TaskComponentModel(
    val checklist: Checklist,
    val tasks: List<BaseTaskListItem>,
    val sectionChecklists: List<ChecklistSectionWithTask>
)