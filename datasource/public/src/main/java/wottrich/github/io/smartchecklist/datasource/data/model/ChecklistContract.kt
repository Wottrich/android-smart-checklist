package wottrich.github.io.smartchecklist.datasource.data.model


interface ChecklistContract {
    val uuid: String
    val name: String
    val parentUuid: String?
    val isSelected: Boolean
}