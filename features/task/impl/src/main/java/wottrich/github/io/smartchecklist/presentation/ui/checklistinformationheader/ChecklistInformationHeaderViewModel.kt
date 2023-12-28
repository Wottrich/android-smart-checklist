package wottrich.github.io.smartchecklist.presentation.ui.checklistinformationheader

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.checklist.domain.ObserveSelectedChecklistUuidUseCase
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.domain.usecase.GetTasksFromSelectedChecklistUseCase

@OptIn(InternalCoroutinesApi::class)
class ChecklistInformationHeaderViewModel(
    private val observeSelectedChecklistUuidUseCase: ObserveSelectedChecklistUuidUseCase,
    private val getTasksFromSelectedChecklistUseCase: GetTasksFromSelectedChecklistUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ChecklistInformationHeaderUiState())
    val uiState = _uiState.asStateFlow()

    private var checklistUuid: String? = null

    init {
        launchIO {
            observeSelectedChecklistUuidUseCase().collect(
                FlowCollector {
                    checklistUuid = it.getOrNull()
                    loadTasksFromSelectedChecklist()
                }
            )
        }
    }
    
    private fun loadTasksFromSelectedChecklist() {
        if (checklistUuid == null) return
        launchIO {
            getTasksFromSelectedChecklistUseCase(checkNotNull(checklistUuid)).onSuccess { tasks ->
                _uiState.value = uiState.value.copy(
                    isLoading = false,
                    checklistName = "TODO fix it",// TODO simpleModel.checklistName,
                    completedTasksCount = getCompletedTasksCount(tasks),
                    totalTasksCount = tasks.size
                )
            }.onFailure { throw it }
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