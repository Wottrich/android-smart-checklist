package wottrich.github.io.smartchecklist.quicklychecklist.presentation.states

sealed class QuicklyChecklistAddNewChecklistUiEffect {
    object OnAddNewChecklistCompleted : QuicklyChecklistAddNewChecklistUiEffect()
    object OnAddNewChecklistFailure : QuicklyChecklistAddNewChecklistUiEffect()
}