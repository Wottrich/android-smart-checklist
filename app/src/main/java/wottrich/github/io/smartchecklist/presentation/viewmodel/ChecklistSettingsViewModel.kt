package wottrich.github.io.smartchecklist.presentation.viewmodel

import androidx.annotation.StringRes
import wottrich.github.io.smartchecklist.R
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.checklist.domain.GetChecklistAsTextUseCase
import wottrich.github.io.smartchecklist.checklist.domain.GetSelectedChecklistUseCase
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus
import wottrich.github.io.smartchecklist.quicklychecklist.domain.ConvertChecklistIntoQuicklyChecklistUseCase
import wottrich.github.io.smartchecklist.quicklychecklist.domain.GetQuicklyChecklistDeepLinkUseCase

class ChecklistSettingsViewModel(
    private val getSelectedChecklistUseCase: GetSelectedChecklistUseCase,
    private val shareChecklistAsTextUseCase: GetChecklistAsTextUseCase,
    private val convertChecklistIntoQuicklyChecklistUseCase: ConvertChecklistIntoQuicklyChecklistUseCase,
    private val getQuicklyChecklistDeepLinkUseCase: GetQuicklyChecklistDeepLinkUseCase
) : BaseViewModel() {

    private val _uiEffect = SingleShotEventBus<ChecklistSettingUiEffect>()
    val uiEffect = _uiEffect.events

    fun onCopyChecklistClicked() {
        launchIO {
            val checklist = checkNotNull(getSelectedChecklistUseCase().getOrNull())
            shareChecklistAsTextUseCase(checklist.uuid).onSuccess {
                _uiEffect.emit(ChecklistSettingUiEffect.ShareChecklistAsText(it))
            }.onFailure {
                _uiEffect.emit(ChecklistSettingUiEffect.SnackbarError(R.string.checklist_settings_error_copy_checklist))
            }
        }
    }

    fun onShareChecklistClicked() {
        launchIO {
            val checklist = checkNotNull(getSelectedChecklistUseCase().getOrNull())
            convertChecklistIntoQuicklyChecklistUseCase(checklist.uuid).onSuccess {
                getQuicklyChecklistDeepLinkUseCase(it).onSuccess { deeplink ->
                    _uiEffect.emit(ChecklistSettingUiEffect.ShareChecklistAsText(deeplink))
                }.onFailure {
                    _uiEffect.emit(ChecklistSettingUiEffect.SnackbarError(R.string.checklist_settings_error_share_checklist))
                }
            }.onFailure {
                _uiEffect.emit(ChecklistSettingUiEffect.SnackbarError(R.string.checklist_settings_error_share_checklist))
            }
        }
    }
}

sealed class ChecklistSettingUiEffect {
    data object CloseScreen : ChecklistSettingUiEffect()
    data class ShareChecklistAsText(val text: String) : ChecklistSettingUiEffect()
    data class SnackbarError(@StringRes val textRes: Int) : ChecklistSettingUiEffect()
}