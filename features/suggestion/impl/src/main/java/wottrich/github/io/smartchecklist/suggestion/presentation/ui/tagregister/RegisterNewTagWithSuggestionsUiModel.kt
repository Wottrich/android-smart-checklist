package wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagregister

data class RegisterNewTagWithSuggestionsUiModel(
    val isEditMode: Boolean = true,
    val shouldShowRegisterNewTagButton: Boolean = false,
    val tagName: String = "",
    val suggestionName: String = "",
    val suggestions: List<String> = listOf() // TODO improve
)