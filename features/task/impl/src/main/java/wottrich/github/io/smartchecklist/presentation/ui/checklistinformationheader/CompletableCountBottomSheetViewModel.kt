package wottrich.github.io.smartchecklist.presentation.ui.checklistinformationheader

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.checklist.domain.GetSelectedChecklistUseCase
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.domain.usecase.GetTasksUseCase

class CompletableCountBottomSheetViewModel(
    getSelectedChecklistUseCase: GetSelectedChecklistUseCase,
    getTasksUseCase: GetTasksUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(CompletableCountUiState())
    val uiState = _uiState.asStateFlow()

    init {
        launchIO {
            val checklistUuid = checkNotNull(
                getSelectedChecklistUseCase().getOrNull()?.uuid
            )
            getTasksUseCase(checklistUuid).onSuccess {
                onGetTasksSuccess(it)
            }.onFailure {
                throw it
            }
        }
    }

    private fun onGetTasksSuccess(tasks: List<Task>) {
        val completedTasksCount = tasks.filter { it.isCompleted }.size
        _uiState.value = uiState.value.copy(
            isLoading = false,
            completedTasksCount = completedTasksCount,
            totalTasksCount = tasks.size
        )
    }

}

data class CompletableCountUiState(
    val isLoading: Boolean = true,
    val completedTasksCount: Int = 0,
    val totalTasksCount: Int = 0
)