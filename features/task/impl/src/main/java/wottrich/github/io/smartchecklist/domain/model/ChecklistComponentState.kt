package wottrich.github.io.smartchecklist.domain.model

sealed class ChecklistComponentState {
    data object Loading : ChecklistComponentState()
    data class Success(val data: TaskComponentModel) : ChecklistComponentState()
}