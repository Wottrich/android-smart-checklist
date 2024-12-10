package wottrich.github.io.smartchecklist.presentation.viewmodel

import androidx.annotation.StringRes
import wottrich.github.io.smartchecklist.R
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.checklist.domain.GetChecklistAsTextUseCase
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus

class ChecklistSettingsViewModel(
    private val shareChecklistAsTextUseCase: GetChecklistAsTextUseCase,
) : BaseViewModel() {

    private val _uiEffect = SingleShotEventBus<ChecklistSettingUiEffect>()
    val uiEffect = _uiEffect.events

    fun onCopyChecklistClicked() {
        launchIO {
            shareChecklistAsTextUseCase().onSuccess {
                _uiEffect.emit(ChecklistSettingUiEffect.ShareChecklistAsText(it))
            }.onFailure {
                _uiEffect.emit(ChecklistSettingUiEffect.SnackbarError(R.string.checklist_settings_error_copy_checklist))
            }
        }
    }
}

sealed class ChecklistSettingUiEffect {
    data object CloseScreen : ChecklistSettingUiEffect()
    data class ShareChecklistAsText(val text: String) : ChecklistSettingUiEffect()
    data class SnackbarError(@StringRes val textRes: Int) : ChecklistSettingUiEffect()
}