package wottrich.github.io.quicklychecklist.impl.presentation.states

sealed class QuicklyChecklistConfirmUiEffect {
    data class OnShareChecklistBack(val encodedQuicklyChecklist: String) :
        QuicklyChecklistConfirmUiEffect()

    data class OnSaveNewChecklist(val quicklyChecklistJson: String) :
        QuicklyChecklistConfirmUiEffect()

    object FailureShareChecklistBack : QuicklyChecklistConfirmUiEffect()
}