package wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagregister

fun interface RegisterNewTagWithSuggestionsViewModelAction {
    fun sendAction(action: Action)

    sealed class Action {
        data class OnTagNameTextChange(val text: String) : Action()
        data class OnSuggestionNameTextChange(val text: String) : Action()
        object OnDoneTagNameClicked : Action()
        object OnAddSuggestionClicked : Action()
    }
}