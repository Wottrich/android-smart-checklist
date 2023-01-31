package wottrich.github.io.quicklychecklist.impl.presentation.states

sealed class QuicklyChecklistAddNewChecklistUiEffect {
    object OnAddNewChecklistCompleted : QuicklyChecklistAddNewChecklistUiEffect()
    object OnAddNewChecklistFailure : QuicklyChecklistAddNewChecklistUiEffect()
}