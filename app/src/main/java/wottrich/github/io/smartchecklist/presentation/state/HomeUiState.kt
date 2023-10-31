package wottrich.github.io.smartchecklist.presentation.state

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Overview(
        val isEditing: Boolean = false,
        val completedTasksCount: Int = 0,
        val totalTasksCount: Int = 0
    ) : HomeUiState()
    object Empty : HomeUiState()
}