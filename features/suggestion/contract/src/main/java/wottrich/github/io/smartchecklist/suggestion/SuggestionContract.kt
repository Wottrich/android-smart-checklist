package wottrich.github.io.smartchecklist.suggestion

interface SuggestionContract {
    val parentUuid: String
    val uuid: String
    val name: String
    val isEnabled: Boolean
}