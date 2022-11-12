package wottrich.github.io.quicklychecklist.impl.presentation.viewmodels

import github.io.wottrich.newchecklist.domain.AddNewChecklistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wottrich.github.io.datasource.entity.NewChecklist
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.datasource.entity.QuicklyChecklist
import wottrich.github.io.impl.domain.usecase.AddManyTasksUseCase
import wottrich.github.io.tools.base.BaseViewModel

class QuicklyChecklistAddNewChecklistViewModel(
    private val addNewChecklistUseCase: AddNewChecklistUseCase,
    private val addManyTasksUseCase: AddManyTasksUseCase,
    private val quicklyChecklist: QuicklyChecklist
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(QuicklyChecklistAddNewChecklistUiState())
    val uiState: StateFlow<QuicklyChecklistAddNewChecklistUiState> = _uiState

    fun onTextChange(typedText: String) {
        _uiState.value = uiState.value.copy(
            checklistName = typedText,
            isButtonEnabled = typedText.isNotEmpty()
        )
    }

    fun onConfirmButtonClicked() {
        launchIO {
            val newChecklist = NewChecklist(name = uiState.value.checklistName)
            val newTasks = getTasksWithChecklistId(newChecklist.uuid)
            addNewChecklistUseCase(newChecklist)
            addManyTasksUseCase(newTasks)
        }
    }

    private fun getTasksWithChecklistId(uuid: String): List<NewTask> {
        return quicklyChecklist.tasks.map {
            it.copy(parentUuid = uuid)
        }
    }
}

data class QuicklyChecklistAddNewChecklistUiState(
    val checklistName: String = "",
    val isButtonEnabled: Boolean = false
)