package wottrich.github.io.smartchecklist.presentation.ui.checklistinformationheader

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.datasource.entity.NewTask
import wottrich.github.io.smartchecklist.domain.usecase.GetTasksFromSelectedChecklistUseCase

class ChecklistInformationHeaderViewModel(
    private val getTasksFromSelectedChecklistUseCase: GetTasksFromSelectedChecklistUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ChecklistInformationHeaderUiState())
    val uiState = _uiState.asStateFlow()

    init {
        launchIO {
            getTasksFromSelectedChecklistUseCase().collect { result ->
                result.onSuccess { simpleModel ->
                    _uiState.value = uiState.value.copy(
                        isLoading = false,
                        checklistName = simpleModel.checklistName,
                        completedTasksCount = getCompletedTasksCount(simpleModel.tasks),
                        totalTasksCount = simpleModel.tasks.size
                    )
                }.onFailure {
                    throw it
                }
            }
        }
    }

    private fun getCompletedTasksCount(tasks: List<NewTask>): Int {
        return tasks.filter { it.isCompleted }.size
    }
}

data class ChecklistInformationHeaderUiState(
    val isLoading: Boolean = true,
    val checklistName: String = "",
    val completedTasksCount: Int = 0,
    val totalTasksCount: Int = 0
)