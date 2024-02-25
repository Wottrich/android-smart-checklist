package wottrich.github.io.smartchecklist.datasource.data.model

interface ChecklistWithNewTasksContract {
    val checklist: ChecklistContract
    val tasks: List<TaskContract>
}