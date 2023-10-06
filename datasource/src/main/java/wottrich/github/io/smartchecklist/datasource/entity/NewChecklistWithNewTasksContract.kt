package wottrich.github.io.smartchecklist.datasource.entity

interface NewChecklistWithNewTasksContract {
    val newChecklist: ChecklistContract
    val newTasks: List<TaskContract>
}