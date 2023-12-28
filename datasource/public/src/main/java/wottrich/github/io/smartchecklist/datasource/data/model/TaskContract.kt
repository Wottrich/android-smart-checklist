package wottrich.github.io.smartchecklist.datasource.data.model

interface TaskContract {
    val uuid: String
    val parentUuid: String
    val name: String
    val isCompleted: Boolean
}