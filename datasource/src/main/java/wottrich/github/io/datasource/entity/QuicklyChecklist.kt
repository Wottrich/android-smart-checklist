package wottrich.github.io.datasource.entity

data class QuicklyChecklist(
    val checklistUuid: String = "",
    val tasks: List<NewTask> = listOf()
)
