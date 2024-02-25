package wottrich.github.io.smartchecklist.quicklychecklist.presentation.viewmodels

import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.datasource.data.model.QuicklyChecklist
import wottrich.github.io.smartchecklist.datasource.data.model.QuicklyTask
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus
import wottrich.github.io.smartchecklist.quicklychecklist.R
import wottrich.github.io.smartchecklist.quicklychecklist.domain.ConvertQuicklyChecklistIntoJsonUseCase
import wottrich.github.io.smartchecklist.quicklychecklist.presentation.states.QuicklyChecklistUiEffect
import wottrich.github.io.smartchecklist.quicklychecklist.presentation.states.QuicklyChecklistUiState

class QuicklyChecklistViewModel(
    private val quicklyChecklistJson: String,
    private val convertQuicklyChecklistIntoJsonUseCase: ConvertQuicklyChecklistIntoJsonUseCase
) : BaseViewModel() {

    private var quicklyChecklist: QuicklyChecklist? = null

    private val _state = MutableStateFlow(QuicklyChecklistUiState())
    val state = _state.asStateFlow()

    private val _effects = SingleShotEventBus<QuicklyChecklistUiEffect>()
    val effects: Flow<QuicklyChecklistUiEffect> = _effects.events

    var tasks = mutableStateListOf<Task>()
        private set

    init {
        initQuicklyChecklistJson()
    }

    fun onConfirmBottomSheetEdit() {
        val quicklyChecklist = this.quicklyChecklist
        if (quicklyChecklist != null) {
            launchIO {
                convertQuicklyChecklistIntoJsonUseCase(quicklyChecklist).onSuccess {
                    launchMain {
                        _effects.emit(QuicklyChecklistUiEffect.OnConfirmQuicklyChecklist(it))
                    }
                }.onFailure {
                    launchMain {
                        _effects.emit(QuicklyChecklistUiEffect.SnackbarError(R.string.quickly_checklist_share_error))
                    }
                }
            }
        }
    }

    fun onCheckChange(newTask: Task) {
        val state = state.value
        val newTaskList = tasks.toList().toMutableList().apply {
            val elementIndex = indexOf(newTask)
            getOrNull(elementIndex)?.copy(isCompleted = !newTask.isCompleted)?.let {
                this[elementIndex] = it
            }
        }
        updateTasks(newTaskList)
        _state.value = state.copy(tasks = tasks)
    }

    private fun initQuicklyChecklistJson() {
        val quicklyChecklist = getQuicklyChecklistFromJson()
        this.quicklyChecklist = quicklyChecklist
        if (quicklyChecklist != null && quicklyChecklist.checklistUuid.isNotEmpty()) {
            handleQuicklyChecklistState(quicklyChecklist)
        } else {
            handleInvalidChecklist()
        }
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

    private fun handleQuicklyChecklistState(quicklyChecklist: QuicklyChecklist) {
        _state.update {
            val tasks = quicklyChecklist.getConvertedTasks()
            updateTasks(tasks)
            it.copy(tasks = tasks)
        }
    }

    private fun handleInvalidChecklist() {
        launchMain {
            _effects.emit(QuicklyChecklistUiEffect.InvalidChecklist)
        }
    }

    private fun updateTasks(newTasks: List<Task>) {
        quicklyChecklist = quicklyChecklist?.copy(tasks = newTasks.geConvertedQuicklyChecklist())
        tasks.clear()
        tasks.addAll(newTasks)
    }

    private fun List<Task>.geConvertedQuicklyChecklist() = this.map {
        QuicklyTask(name = it.name, isCompleted = it.isCompleted)
    }
}