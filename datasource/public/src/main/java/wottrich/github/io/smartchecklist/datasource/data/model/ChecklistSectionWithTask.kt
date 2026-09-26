package wottrich.github.io.smartchecklist.datasource.data.model

data class ChecklistSectionWithTask(
    override val checklistSection: Checklist,
    override val tasks: List<Task>
) : ChecklistSectionWithTaskContract
