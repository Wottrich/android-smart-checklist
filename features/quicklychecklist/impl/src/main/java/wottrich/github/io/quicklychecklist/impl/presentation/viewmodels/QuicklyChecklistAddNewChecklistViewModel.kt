package wottrich.github.io.quicklychecklist.impl.presentation.viewmodels

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import github.io.wottrich.newchecklist.domain.AddNewChecklistUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wottrich.github.io.datasource.entity.NewChecklist
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.datasource.entity.QuicklyChecklist
import wottrich.github.io.impl.domain.usecase.AddManyTasksUseCase
import wottrich.github.io.quicklychecklist.impl.presentation.states.QuicklyChecklistAddNewChecklistUiEffect
import wottrich.github.io.tools.SingleShotEventBus
import wottrich.github.io.tools.base.BaseViewModel
import wottrich.github.io.tools.base.UuidGenerator
import wottrich.github.io.tools.base.onFailure
import wottrich.github.io.tools.base.onSuccess

class QuicklyChecklistAddNewChecklistViewModel(
    private val addNewChecklistUseCase: AddNewChecklistUseCase,
    private val addManyTasksUseCase: AddManyTasksUseCase,
    private val quicklyChecklistJson: String
) : BaseViewModel() {

    private var quicklyChecklist: QuicklyChecklist? = null

    private val _uiState = MutableStateFlow(QuicklyChecklistAddNewChecklistUiState())
    val uiState: StateFlow<QuicklyChecklistAddNewChecklistUiState> = _uiState

    private val _effects = SingleShotEventBus<QuicklyChecklistAddNewChecklistUiEffect>()
    val effects: Flow<QuicklyChecklistAddNewChecklistUiEffect> = _effects.events

    init {
        initQuicklyChecklistAddNewChecklist()
    }

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
            addNewChecklistUseCase(newChecklist).onSuccess {
                handleAddNewTasksInNewChecklist(newTasks)
            }.onFailure {
                emitOnAddNewChecklistFailure()
            }
        }
    }

    private fun handleAddNewTasksInNewChecklist(newTasks: List<NewTask>) {
        launchIO {
            addManyTasksUseCase(newTasks).onSuccess {
                emitOnAddNewChecklistCompleted()
            }.onFailure {
                emitOnAddNewChecklistFailure()
            }
        }
    }

    private suspend fun emitOnAddNewChecklistCompleted() {
        _effects.emit(QuicklyChecklistAddNewChecklistUiEffect.OnAddNewChecklistCompleted)
    }

    private suspend fun emitOnAddNewChecklistFailure() {
        _effects.emit(QuicklyChecklistAddNewChecklistUiEffect.OnAddNewChecklistFailure)
    }

    private fun getTasksWithChecklistId(parentUuid: String): List<NewTask> {
        return quicklyChecklist?.getConvertedTasks()
            ?.map {
                it.copy(uuid = UuidGenerator.getRandomUuid(), parentUuid = parentUuid)
            }.orEmpty()
    }

    private fun initQuicklyChecklistAddNewChecklist() {
        quicklyChecklist = getQuicklyChecklistFromJson()
    }

    private fun getQuicklyChecklistFromJson(): QuicklyChecklist? {
        return try {
            Gson().fromJson<QuicklyChecklist>(
                quicklyChecklistJson,
                object : TypeToken<QuicklyChecklist>() {}.type
            )
        } catch (ex: Exception) {
            null
        }
    }
}

data class QuicklyChecklistAddNewChecklistUiState(
    val checklistName: String = "",
    val isButtonEnabled: Boolean = false
)