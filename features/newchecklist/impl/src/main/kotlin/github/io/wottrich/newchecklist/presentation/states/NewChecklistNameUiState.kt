package github.io.wottrich.newchecklist.presentation.states

data class NewChecklistNameUiState(
    val checklistName: String,
    val isDoneButtonEnabled: Boolean
) {
    companion object {
        val Initial = NewChecklistNameUiState(checklistName = "", isDoneButtonEnabled = false)
    }
}