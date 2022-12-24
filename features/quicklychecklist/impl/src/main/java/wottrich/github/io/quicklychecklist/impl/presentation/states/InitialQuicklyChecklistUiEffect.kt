package wottrich.github.io.quicklychecklist.impl.presentation.states

sealed class InitialQuicklyChecklistUiEffect {
    data class OnQuicklyChecklistJson(val quicklyChecklistJson: String) :
        InitialQuicklyChecklistUiEffect()
    object OnInvalidChecklist : InitialQuicklyChecklistUiEffect()
}