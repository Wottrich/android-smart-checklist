package wottrich.github.io.smartchecklist.datasource.entity

data class SelectedChecklistTasksSimpleModel(
    val parentUuid: String,
    val tasks: List<NewTask>
)