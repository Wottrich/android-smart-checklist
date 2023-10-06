package wottrich.github.io.smartchecklist.datasource.entity


interface ChecklistContract {
    val uuid: String
    val name: String
    val isSelected: Boolean
}