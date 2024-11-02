package wottrich.github.io.smartchecklist.domain.model

import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistSectionWithTask
import wottrich.github.io.smartchecklist.datasource.data.model.Task

data class TaskComponentModel(
    val tasks: List<Task>,
    val sectionChecklists: List<ChecklistSectionWithTask>
)