package wottrich.github.io.quicklychecklist.impl.presentation.states

import androidx.annotation.StringRes
import wottrich.github.io.datasource.entity.QuicklyChecklist

sealed class QuicklyChecklistUiEffect {
    object InvalidChecklist : QuicklyChecklistUiEffect()

    data class OnShareChecklistBack(val quicklyChecklistJson: String) : QuicklyChecklistUiEffect()

    data class OnSaveNewChecklist(val quicklyChecklist: QuicklyChecklist) :
        QuicklyChecklistUiEffect()

    data class SnackbarError(@StringRes val messageError: Int) : QuicklyChecklistUiEffect()
}