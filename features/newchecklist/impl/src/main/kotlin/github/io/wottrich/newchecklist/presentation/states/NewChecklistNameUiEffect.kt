package github.io.wottrich.newchecklist.presentation.states

sealed class NewChecklistNameUiEffect {
    object CloseScreen : NewChecklistNameUiEffect()
    object InvalidChecklistState : NewChecklistNameUiEffect()
}