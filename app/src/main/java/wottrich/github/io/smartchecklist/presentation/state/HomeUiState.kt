package wottrich.github.io.smartchecklist.presentation.state

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Overview(val isEditing: Boolean = false) : HomeUiState()
    object Empty : HomeUiState()
}