package wottrich.github.io.smartchecklist.datasource.entity

interface TaskContract {
    val uuid: String
    val parentUuid: String
    val name: String
    val isCompleted: Boolean
}