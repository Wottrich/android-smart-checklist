package wottrich.github.io.smartchecklist.suggestion

interface TagSuggestionEmbeddedContract {
    val tag: TagContract
    val suggestions: List<SuggestionContract>
}