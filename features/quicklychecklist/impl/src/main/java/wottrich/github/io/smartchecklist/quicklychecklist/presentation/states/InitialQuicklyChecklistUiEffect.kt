package wottrich.github.io.smartchecklist.quicklychecklist.presentation.states

sealed class InitialQuicklyChecklistUiEffect {
    data class OnQuicklyChecklistJson(val quicklyChecklistJson: String) :
        InitialQuicklyChecklistUiEffect()
    object OnInvalidChecklist : InitialQuicklyChecklistUiEffect()
}