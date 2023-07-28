package wottrich.github.io.smartchecklist.presentation.state

import androidx.annotation.StringRes


sealed class HomeUiEffects {
    data class SnackbarTaskCompleted(val taskName: String) : HomeUiEffects()
    data class SnackbarTaskUncompleted(val taskName: String) : HomeUiEffects()
    object SnackbarChecklistDelete : HomeUiEffects()
    data class SnackbarError(@StringRes val errorMessage: Int) : HomeUiEffects()
    data class OnShareQuicklyChecklist(val quicklyChecklistJson: String) : HomeUiEffects()
}