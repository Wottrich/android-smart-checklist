package wottrich.github.io.datasource.entity


interface ChecklistContract {
    val uuid: String
    var name: String
    var isSelected: Boolean
}