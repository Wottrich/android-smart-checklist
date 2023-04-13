package wottrich.github.io.impl.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.impl.R
import wottrich.github.io.impl.domain.usecase.AddTaskToDatabaseUseCase
import wottrich.github.io.impl.domain.usecase.GetChangeTaskStatusUseCase
import wottrich.github.io.impl.domain.usecase.GetDeleteTaskUseCase
import wottrich.github.io.impl.domain.usecase.GetTasksFromSelectedChecklistUseCase
import wottrich.github.io.impl.presentation.action.TaskComponentViewModelAction
import wottrich.github.io.impl.presentation.action.TaskComponentViewModelAction.Action
import wottrich.github.io.impl.presentation.action.TaskComponentViewModelAction.Action.AddTask
import wottrich.github.io.impl.presentation.action.TaskComponentViewModelAction.Action.ChangeTaskStatus
import wottrich.github.io.impl.presentation.action.TaskComponentViewModelAction.Action.DeleteTask
import wottrich.github.io.impl.presentation.state.TaskComponentUiState
import wottrich.github.io.impl.presentation.viewmodel.TaskComponentViewModelUiEffect.OnError
import wottrich.github.io.tools.SingleShotEventBus
import wottrich.github.io.tools.base.BaseViewModel
import wottrich.github.io.tools.base.onFailure
import wottrich.github.io.tools.base.onSuccess

@OptIn(InternalCoroutinesApi::class)
class TaskComponentViewModel(
    private val getTasksFromSelectedChecklistUseCase: GetTasksFromSelectedChecklistUseCase,
    private val addTaskToDatabaseUseCase: AddTaskToDatabaseUseCase,
    private val getChangeTaskStatusUseCase: GetChangeTaskStatusUseCase,
    private val getDeleteTaskUseCase: GetDeleteTaskUseCase,
) : BaseViewModel(), TaskComponentViewModelAction {

    private var checklistUuidReference: String? = null

    var tasks = mutableStateListOf<NewTask>()
        private set

    private val _uiState = MutableStateFlow(TaskComponentUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = SingleShotEventBus<TaskComponentViewModelUiEffect>()
    val uiEffect: Flow<TaskComponentViewModelUiEffect> = _uiEffect.events

    init {
        launchIO {
            getTasksFromSelectedChecklistUseCase().collect(
                FlowCollector { result ->
                    val simpleModel = checkNotNull(result.getOrNull())
                    checklistUuidReference = simpleModel.parentUuid
                    withContext(main()) {
                        tasks.clear()
                        tasks.addAll(simpleModel.tasks)
                    }
                }
            )
        }
    }

    override fun sendAction(action: Action) {
        when (action) {
            is AddTask -> handleAddTaskAction()
            is ChangeTaskStatus -> handleChangeTaskStatus(action.task)
            is DeleteTask -> handleDeleteTask(action.task)
            is Action.OnTextChanged -> {
                _uiState.value = _uiState.value.copy(
                    taskName = action.text
                )
            }
        }
    }

    private fun handleAddTaskAction() {
        launchIO {
            val checklistUuid = checkNotNull(checklistUuidReference)
            val taskName = checkNotNull(uiState.value).taskName.ifEmpty {
                return@launchIO onFailureInsertTask()
            }
            insertValidTaskOnDatabase(taskName, checklistUuid)
        }
    }

    private suspend fun insertValidTaskOnDatabase(taskName: String, checklistUuid: String) {
        addTaskToDatabaseUseCase(
            AddTaskToDatabaseUseCase.Params(
                parentUuid = checklistUuid,
                taskName = taskName
            )
        ).onSuccess {
            _uiState.value = _uiState.value.copy(
                taskName = ""
            )
        }.onFailure {
            _uiEffect.emit(OnError(stringRes = R.string.checklist_add_new_item_failure))
        }
    }

    private suspend fun onFailureInsertTask() {
        _uiEffect.emit(OnError(stringRes = R.string.checklist_add_new_item_failure))
    }

    private fun handleChangeTaskStatus(task: NewTask) {
        launchIO {
            getChangeTaskStatusUseCase(task)
        }
    }

    private fun handleDeleteTask(task: NewTask) {
        launchIO {
            getDeleteTaskUseCase(task)
        }
    }
}

sealed class TaskComponentViewModelUiEffect {
    data class OnError(@StringRes val stringRes: Int) : TaskComponentViewModelUiEffect()
}