package wottrich.github.io.smartchecklist.presentation.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.smartchecklist.presentation.viewmodel.ChecklistSettingsAllTasksAction.CHECK_ALL
import wottrich.github.io.smartchecklist.presentation.viewmodel.ChecklistSettingsAllTasksAction.UNCHECK_ALL
import wottrich.github.io.smartchecklist.domain.usecase.ChangeTasksCompletedStatusUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetTasksUseCase
import github.io.wottrich.kotlin.SingleShotEventBus
import github.io.wottrich.android.BaseViewModel
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders

class ChecklistSettingsViewModel(
    dispatchersProviders: DispatchersProviders,
    private val checklistId: String,
    private val getTasksUseCase: GetTasksUseCase,
    private val changeTasksCompletedStatusUseCase: ChangeTasksCompletedStatusUseCase
) : BaseViewModel(dispatchersProviders) {

    private val _uiState = MutableStateFlow(ChecklistSettingUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = SingleShotEventBus<ChecklistSettingUiEffect>()
    val uiEffect = _uiEffect.events

    fun onChangeSwitcher(tasksAction: ChecklistSettingsAllTasksAction) {
        when (tasksAction) {
            CHECK_ALL -> onCheckAllTasksClicked()
            UNCHECK_ALL -> onUncheckAllTasksClicked()
        }
    }

    private fun onUncheckAllTasksClicked() {
        val allTasksActionValue = if (uiState.value.allTasksAction == UNCHECK_ALL) null
        else UNCHECK_ALL
        _uiState.value = uiState.value.copy(
            allTasksAction = allTasksActionValue,
            isConfirmButtonEnabled = allTasksActionValue != null
        )
    }

    private fun onCheckAllTasksClicked() {
        val allTasksActionValue = if (uiState.value.allTasksAction == CHECK_ALL) null
        else CHECK_ALL
        _uiState.value = uiState.value.copy(
            allTasksAction = allTasksActionValue,
            isConfirmButtonEnabled = allTasksActionValue != null
        )
    }

    fun onConfirmClicked() {
        launchIO {
            val tasks = getTasksUseCase(checklistId).getOrNull().orEmpty()
            changeTasksCompletedStatusUseCase(
                ChangeTasksCompletedStatusUseCase.Params(
                    tasks = tasks,
                    isCompleted = uiState.value.allTasksAction == CHECK_ALL
                )
            )
            _uiEffect.emit(ChecklistSettingUiEffect.CloseScreen)
        }
    }
}

sealed class ChecklistSettingUiEffect {
    object CloseScreen : ChecklistSettingUiEffect()
}

data class ChecklistSettingUiState(
    val allTasksAction: ChecklistSettingsAllTasksAction? = null,
    val isConfirmButtonEnabled: Boolean = false
)

enum class ChecklistSettingsAllTasksAction {
    UNCHECK_ALL,
    CHECK_ALL
}