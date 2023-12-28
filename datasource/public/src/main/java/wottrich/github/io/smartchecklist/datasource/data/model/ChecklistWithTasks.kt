package wottrich.github.io.smartchecklist.datasource.data.model

data class ChecklistWithTasks(
    override val checklist: Checklist,
    override val tasks: List<Task>
) : ChecklistWithNewTasksContract {
    override fun toString(): String {
        val string = StringBuilder()

        string.append("Checklist: ")
        string.append(checklist.name)
        string.appendLine()

        tasks.forEach {
            val isCompleted = if(it.isCompleted) "✓ - " else "✗ - "
            string.append(isCompleted)
            string.appendLine(it.name)
        }

        return string.toString()
    }
}
