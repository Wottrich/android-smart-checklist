package wottrich.github.io.androidsmartchecklist.presentation.ui.model

data class HomeDrawerChecklistItemModel(
    val checklistUuid: String,
    val checklistName: String,
    val completeCountLabel: String,
    val isChecklistSelected: Boolean,
    val hasUncompletedTask: Boolean,
)
