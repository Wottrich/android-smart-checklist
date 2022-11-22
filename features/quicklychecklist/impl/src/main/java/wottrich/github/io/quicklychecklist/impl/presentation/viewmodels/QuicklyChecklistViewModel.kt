package wottrich.github.io.quicklychecklist.impl.presentation.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.datasource.entity.QuicklyChecklist
import wottrich.github.io.quicklychecklist.impl.R
import wottrich.github.io.quicklychecklist.impl.domain.ConvertQuicklyChecklistIntoJsonUseCase
import wottrich.github.io.quicklychecklist.impl.presentation.states.QuicklyChecklistUiEffect
import wottrich.github.io.quicklychecklist.impl.presentation.states.QuicklyChecklistUiState
import wottrich.github.io.tools.SingleShotEventBus
import wottrich.github.io.tools.base.BaseViewModel
import wottrich.github.io.tools.base.onFailure
import wottrich.github.io.tools.base.onSuccess

class QuicklyChecklistViewModel(
    private val quicklyChecklistJson: String,
    private val convertQuicklyChecklistIntoJsonUseCase: ConvertQuicklyChecklistIntoJsonUseCase
) : BaseViewModel() {

    private var quicklyChecklist: QuicklyChecklist? = null

    private val _state = MutableStateFlow(QuicklyChecklistUiState())
    val state = _state.asStateFlow()

    private val _effects = SingleShotEventBus<QuicklyChecklistUiEffect>()
    val effects: Flow<QuicklyChecklistUiEffect> = _effects.events

    var isSaveChecklistBottomSheet = mutableStateOf(false)
    private set

    var tasks = mutableStateListOf<NewTask>()
        private set

    init {
        initQuicklyChecklistJson()
    }

    fun onCheckChange(newTask: NewTask) {
        val state = state.value
        val newTaskList = tasks.toList().apply {
            val elementIndex = indexOf(newTask)
            val element = this.getOrNull(elementIndex)
            element?.isCompleted = !newTask.isCompleted
        }
        updateTasks(newTaskList)
        _state.value = state.copy(tasks = tasks)
    }

    fun onShareChecklistBackClick() {
        val quicklyChecklist = this.quicklyChecklist
        if (quicklyChecklist != null) {
            launchIO {
                convertQuicklyChecklistIntoJsonUseCase(quicklyChecklist).onSuccess {
                    launchMain {
                        _effects.emit(QuicklyChecklistUiEffect.OnShareChecklistBack(it))
                    }
                }.onFailure {
                    launchMain {
                        _effects.emit(QuicklyChecklistUiEffect.ShowSnackbar(R.string.quickly_checklist_share_error))
                    }
                }
            }
        }
    }

    fun onSaveNewChecklist() {
        isSaveChecklistBottomSheet.value = true
        val quicklyChecklist = this.quicklyChecklist
        if (quicklyChecklist != null) {
            launchMain {
                _effects.emit(QuicklyChecklistUiEffect.OnSaveNewChecklist(quicklyChecklist))
            }
        }
    }

    fun setSaveChecklistBottomSheetFalse(shouldSendSuccessMessage: Boolean = false) {
        if (shouldSendSuccessMessage) {
            launchMain {
                _effects.emit(QuicklyChecklistUiEffect.ShowSnackbar(R.string.quickly_checklist_options_save_new_checklist_success_snackbar))
            }
        }
        isSaveChecklistBottomSheet.value = false
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
            updateTasks(quicklyChecklist.tasks)
            it.copy(tasks = quicklyChecklist.tasks)
        }
    }

    private fun handleInvalidChecklist() {
        launchMain {
            _effects.emit(QuicklyChecklistUiEffect.InvalidChecklist)
        }
    }

    private fun updateTasks(newTasks: List<NewTask>) {
        tasks.clear()
        tasks.addAll(newTasks)
    }
}