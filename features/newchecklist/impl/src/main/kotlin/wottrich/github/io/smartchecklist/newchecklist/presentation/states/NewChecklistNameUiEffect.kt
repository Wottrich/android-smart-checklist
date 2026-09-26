package wottrich.github.io.smartchecklist.newchecklist.presentation.states

sealed class NewChecklistNameUiEffect {
    data object CloseScreen : NewChecklistNameUiEffect()
    data object CreateChecklistFailed : NewChecklistNameUiEffect()
}