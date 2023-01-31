package wottrich.github.io.quicklychecklist.impl.presentation.states

import androidx.annotation.StringRes

sealed class QuicklyChecklistUiEffect {
    object InvalidChecklist : QuicklyChecklistUiEffect()

    data class OnConfirmQuicklyChecklist(val quicklyChecklistJson: String) :
        QuicklyChecklistUiEffect()

    data class SnackbarError(@StringRes val messageError: Int) : QuicklyChecklistUiEffect()
}