package wottrich.github.io.androidsmartchecklist.presentation.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.ChecklistSettingsAllTasksAction.CHECK_ALL
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.ChecklistSettingsAllTasksAction.UNCHECK_ALL
import wottrich.github.io.tools.base.BaseViewModel
import wottrich.github.io.tools.dispatcher.DispatchersProviders

class ChecklistSettingsViewModel(
    dispatchersProviders: DispatchersProviders
) : BaseViewModel(dispatchersProviders) {

    private val _uiState = MutableStateFlow(ChecklistSettingUiState())
    val uiState = _uiState.asStateFlow()

    fun onUncheckAllTasksClicked() {
        val allTasksActionValue = if (uiState.value.allTasksAction == UNCHECK_ALL) null
        else UNCHECK_ALL
        _uiState.value = uiState.value.copy(
            allTasksAction = allTasksActionValue,
            isConfirmButtonEnabled = allTasksActionValue != null
        )
    }

    fun onCheckAllTasksClicked() {
        val allTasksActionValue = if (uiState.value.allTasksAction == CHECK_ALL) null
        else CHECK_ALL
        _uiState.value = uiState.value.copy(
            allTasksAction = allTasksActionValue,
            isConfirmButtonEnabled = allTasksActionValue != null
        )
    }

    fun onConfirmSelection() {

    }

}

data class ChecklistSettingUiState(
    val allTasksAction: ChecklistSettingsAllTasksAction? = null,
    val isConfirmButtonEnabled: Boolean = false
)

enum class ChecklistSettingsAllTasksAction {
    UNCHECK_ALL,
    CHECK_ALL
}