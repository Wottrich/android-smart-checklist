package wottrich.github.io.smartchecklist.quicklychecklist.presentation.states

sealed class QuicklyChecklistConfirmUiEffect {
    data class OnShareChecklistBack(val encodedQuicklyChecklist: String) :
        QuicklyChecklistConfirmUiEffect()

    data class OnSaveNewChecklist(val quicklyChecklistJson: String) :
        QuicklyChecklistConfirmUiEffect()

    object FailureShareChecklistBack : QuicklyChecklistConfirmUiEffect()
}