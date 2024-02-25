package wottrich.github.io.smartchecklist.presentation.ui.sort

sealed class SortTaskListUiEffect {
    data object CloseBottomSheet : SortTaskListUiEffect()
    data object SetSortFailureSnackbar : SortTaskListUiEffect()
}