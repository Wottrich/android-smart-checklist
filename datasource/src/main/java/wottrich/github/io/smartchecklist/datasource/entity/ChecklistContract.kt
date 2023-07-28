package wottrich.github.io.smartchecklist.datasource.entity


interface ChecklistContract {
    val uuid: String
    var name: String
    var isSelected: Boolean
}