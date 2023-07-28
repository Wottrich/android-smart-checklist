package wottrich.github.io.smartchecklist.datasource.entity

data class QuicklyChecklist(
    val checklistUuid: String = "",
    val tasks: List<QuicklyTask> = listOf()
) {
    fun getConvertedTasks() = tasks.map {
        NewTask(
            parentUuid = checklistUuid,
            name = it.name,
            isCompleted = it.isCompleted
        )
    }
}

data class QuicklyTask(
    val name: String,
    val isCompleted: Boolean
)
