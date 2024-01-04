package wottrich.github.io.smartchecklist.presentation.ui.checklistinformationheader

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.domain.usecase.ObserveChecklistWithTasksUseCase

@OptIn(InternalCoroutinesApi::class)
class ChecklistInformationHeaderViewModel(
    private val observeChecklistWithTasksUseCase: ObserveChecklistWithTasksUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ChecklistInformationHeaderUiState())
    val uiState = _uiState.asStateFlow()

    init {
        launchIO {
            observeChecklistWithTasksUseCase().collect(
                FlowCollector {
                    it.onSuccess { checklistWithTasks ->
                        _uiState.value = uiState.value.copy(
                            isLoading = false,
                            checklistName = checklistWithTasks.checklist.name,
                            completedTasksCount = getCompletedTasksCount(checklistWithTasks.tasks),
                            totalTasksCount = checklistWithTasks.tasks.size
                        )
                    }
                }
            )
        }
    }

    private fun getCompletedTasksCount(tasks: List<Task>): Int {
        return tasks.filter { it.isCompleted }.size
    }
}

data class ChecklistInformationHeaderUiState(
    val isLoading: Boolean = true,
    val checklistName: String = "",
    val completedTasksCount: Int = 0,
    val totalTasksCount: Int = 0
)