package wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagregister

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import wottrich.github.io.smartchecklist.android.BaseViewModel

class RegisterNewTagWithSuggestionsViewModel : BaseViewModel(), RegisterNewTagWithSuggestionsViewModelAction {

    private val _uiState = MutableStateFlow(RegisterNewTagWithSuggestionsUiModel())
    val uiState = _uiState.asStateFlow()

    override fun sendAction(action: RegisterNewTagWithSuggestionsViewModelAction.Action) {
        when (action) {
            RegisterNewTagWithSuggestionsViewModelAction.Action.OnAddSuggestionClicked -> onAddSuggestionClicked()
            RegisterNewTagWithSuggestionsViewModelAction.Action.OnDoneTagNameClicked -> onDoneTagNameClicked()
            is RegisterNewTagWithSuggestionsViewModelAction.Action.OnSuggestionNameTextChange -> onSuggestionNameTextChange(action.text)
            is RegisterNewTagWithSuggestionsViewModelAction.Action.OnTagNameTextChange -> onTagNameTextChange(action.text)
        }
    }

    private fun onAddSuggestionClicked() {
        _uiState.update {
            val newSuggestions = it.suggestions + it.suggestionName
            it.copy(
                suggestionName = "",
                suggestions = newSuggestions,
                shouldShowRegisterNewTagButton = newSuggestions.isNotEmpty()
            )
        }
    }

    private fun onDoneTagNameClicked() {
        _uiState.update {
            val nextEditModeValue = !it.isEditMode
            it.copy(
                isEditMode = nextEditModeValue
            )
        }
    }

    private fun onSuggestionNameTextChange(suggestionNameText: String) {
        _uiState.update { it.copy(suggestionName = suggestionNameText) }
    }

    private fun onTagNameTextChange(tagNameText: String) {
        _uiState.update { it.copy(tagName = tagNameText) }
    }
}