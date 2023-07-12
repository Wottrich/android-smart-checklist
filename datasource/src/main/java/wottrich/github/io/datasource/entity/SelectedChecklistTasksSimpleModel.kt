package wottrich.github.io.datasource.entity

data class SelectedChecklistTasksSimpleModel(
    val parentUuid: String,
    val tasks: List<NewTask>
)