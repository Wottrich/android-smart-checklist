package wottrich.github.io.smartchecklist.presentation.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.domain.model.ChecklistComponentState
import wottrich.github.io.smartchecklist.domain.usecase.AddTaskToDatabaseUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetChangeTaskStatusUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetDeleteTaskUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetTasksSortedUseCase
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus
import wottrich.github.io.smartchecklist.newchecklist.domain.model.NewChecklistModel
import wottrich.github.io.smartchecklist.newchecklist.domain.usecase.AddNewChecklistUseCase
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction.Action
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction.Action.AddTask
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction.Action.ChangeTaskStatus
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction.Action.DeleteTask
import wottrich.github.io.smartchecklist.presentation.state.TaskComponentUiState
import wottrich.github.io.smartchecklist.presentation.task.model.BaseTaskListItem
import wottrich.github.io.smartchecklist.presentation.viewmodel.TaskComponentViewModelUiEffect.OnError
import wottrich.github.io.smartchecklist.task.R

@OptIn(InternalCoroutinesApi::class)
class TaskComponentViewModel(
    private val getTasksSortedUseCase: GetTasksSortedUseCase,
    private val addNewChecklistUseCase: AddNewChecklistUseCase,
    private val addTaskToDatabaseUseCase: AddTaskToDatabaseUseCase,
    private val getChangeTaskStatusUseCase: GetChangeTaskStatusUseCase,
    private val getDeleteTaskUseCase: GetDeleteTaskUseCase,
) : BaseViewModel(), TaskComponentViewModelAction {

    private var checklistUuidReference: String? = null

    var tasks = mutableStateListOf<BaseTaskListItem>()
        private set

    private val _uiState = MutableStateFlow(TaskComponentUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = SingleShotEventBus<TaskComponentViewModelUiEffect>()
    val uiEffect: Flow<TaskComponentViewModelUiEffect> = _uiEffect.events

    private var sortItemsJob: Job? = null

    init {
        loadSortItems()
    }

    private fun loadSortItems() {
        sortItemsJob?.cancel()
        sortItemsJob = launchIO {
            getTasksSortedUseCase().collect {
                it.onSuccess { state ->
                    when (state) {
                        ChecklistComponentState.Loading -> Unit
                        is ChecklistComponentState.Success -> {
                            val checklistComponentModel = state.data
                            withContext(main()) {
                                checklistUuidReference = checklistComponentModel.checklist.uuid
                                _uiState.value = uiState.value.copy(
                                    checklist = checklistComponentModel,
                                    showSectionButton = checklistComponentModel.checklist.parentUuid == null
                                )
                            }
                        }
                    }
                }.onFailure { error ->
                    Log.d("FAILURE_CHECK", error.message.orEmpty())
                    emitLoadTasksFailure()
                }
            }
        }
    }

    private suspend fun emitLoadTasksFailure() {
        _uiEffect.emit(OnError(stringRes = R.string.task_item_component_load_tasks_error))
    }

    override fun sendAction(action: Action) {
        when (action) {
            AddTask -> handleAddTaskAction()
            Action.AddSection -> handleAddSectionAction()
            is ChangeTaskStatus -> handleChangeTaskStatus(action.task)
            is DeleteTask -> handleDeleteTask(action.task)
            is Action.OnTextChanged -> {
                _uiState.value = _uiState.value.copy(
                    taskName = action.text
                )
            }
        }
    }

    private fun handleAddSectionAction() {
        launchIO {
            val checklistUuid = checkNotNull(checklistUuidReference)
            val checklistName = checkNotNull(uiState.value).taskName.ifEmpty {
                return@launchIO onFailureInsertTask()
            }
            val checklist = NewChecklistModel(
                parentUuid = checklistUuid,
                name = checklistName
            )
            resetNameField()
            addNewChecklistUseCase(checklist).onFailure {
                Log.d("ADD_NEW_CHECKLIST", it.message ?: "Error")
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
        val newTask = Task(
            parentUuid = checklistUuid,
            name = taskName
        )
        resetNameField()
        addTaskToDatabaseUseCase(newTask).onSuccess {
//            loadSortItems()
        }.onFailure {
            _uiEffect.emit(OnError(stringRes = R.string.checklist_add_new_task_unknown_error))
        }
    }

    private suspend fun onFailureInsertTask() {
        _uiEffect.emit(OnError(stringRes = R.string.checklist_add_new_item_failure))
    }

    private fun handleChangeTaskStatus(task: Task) {
        launchIO {
            getChangeTaskStatusUseCase(task).onSuccess {
//                loadSortItems()
            }.onFailure {
                _uiEffect.emit(OnError(stringRes = R.string.checklist_change_task_state_failure))
            }
        }
    }

    private fun handleDeleteTask(task: Task) {
        launchIO {
            getDeleteTaskUseCase(task).onSuccess {
//                loadSortItems()
            }
        }
    }

    private fun resetNameField() {
        _uiState.value = _uiState.value.copy(
            taskName = ""
        )
    }
}

sealed class TaskComponentViewModelUiEffect {
    data class OnError(@StringRes val stringRes: Int) : TaskComponentViewModelUiEffect()
}