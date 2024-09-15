package wottrich.github.io.smartchecklist.datasource.data.model

interface ChecklistSectionWithTaskContract {
    val checklistSection: ChecklistContract
    val tasks: List<TaskContract>
}