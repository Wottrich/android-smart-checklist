package wottrich.github.io.smartchecklist.suggestion

interface TagContract {
    val tagUuid: String
    val name: String
    val isEnabled: Boolean
}