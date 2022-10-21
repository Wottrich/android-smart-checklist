package wottrich.github.io.quicklychecklist.impl.presentation.states

sealed class InitialQuicklyChecklistUiEffect {
    data class OnConfirmButtonClicked(val quicklyChecklistJson: String) :
        InitialQuicklyChecklistUiEffect()
}