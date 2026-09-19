package wottrich.github.io.smartchecklist.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.checklist.domain.ObserveSelectedChecklistUuidUseCase
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.domain.model.SortItemType
import wottrich.github.io.smartchecklist.domain.usecase.AddTaskToDatabaseUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetChangeTaskStatusUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetDeleteTaskUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetTasksFromSelectedChecklistUseCase
import wottrich.github.io.smartchecklist.domain.usecase.ObserveSortItemSelectedUseCase
import wottrich.github.io.smartchecklist.domain.usecase.ReverseTasksIfNeededUseCase
import wottrich.github.io.smartchecklist.domain.usecase.SortTasksBySelectedSortUseCase
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction.Action
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction.Action.AddTask
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction.Action.ChangeTaskStatus
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction.Action.DeleteTask
import wottrich.github.io.smartchecklist.presentation.effect.TaskComponentViewModelUiEffect
import wottrich.github.io.smartchecklist.presentation.state.TaskComponentUiState
import wottrich.github.io.smartchecklist.presentation.task.model.BaseTaskListItem
import wottrich.github.io.smartchecklist.presentation.effect.TaskComponentViewModelUiEffect.OnError
import wottrich.github.io.smartchecklist.presentation.ui.TaskBottomSheetType
import wottrich.github.io.smartchecklist.task.R

@OptIn(InternalCoroutinesApi::class)
class TaskComponentViewModel(
    private val observeSortItemSelectedUseCase: ObserveSortItemSelectedUseCase,
    private val observeSelectedChecklistUuidUseCase: ObserveSelectedChecklistUuidUseCase,
    private val getTasksFromSelectedChecklistUseCase: GetTasksFromSelectedChecklistUseCase,
    private val addTaskToDatabaseUseCase: AddTaskToDatabaseUseCase,
    private val getChangeTaskStatusUseCase: GetChangeTaskStatusUseCase,
    private val getDeleteTaskUseCase: GetDeleteTaskUseCase,
    private val sortTasksBySelectedSortUseCase: SortTasksBySelectedSortUseCase,
    private val reverseTasksIfNeededUseCase: ReverseTasksIfNeededUseCase
) : BaseViewModel(), TaskComponentViewModelAction {

    private var checklistUuidReference: String? = null

    private val _uiState = MutableStateFlow(TaskComponentUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = SingleShotEventBus<TaskComponentViewModelUiEffect>()
    val uiEffect: Flow<TaskComponentViewModelUiEffect> = _uiEffect.events

    private var sortItemsJob: Job? = null

    init {
        launchIO {
            observeSelectedChecklistUuidUseCase().collect(
                FlowCollector {
                    checklistUuidReference = it.getOrNull()
                    loadSortItems()
                }
            )
        }
    }

    override fun sendAction(action: Action) {
        when (action) {
            AddTask -> handleAddTaskAction()
            is ChangeTaskStatus -> handleChangeTaskStatus(action.task)
            is DeleteTask -> handleDeleteTask(action.task)
            is Action.OnTextChanged -> {
                _uiState.value = _uiState.value.copy(
                    taskName = action.text
                )
            }

            Action.OnSortTaskClicked -> onSortTaskClicked()
            Action.OnCompletableCountClicked -> onCompletableCountClicked()
            Action.OnUpdateCloseBottomSheet -> {
                _uiState.value = uiState.value.copy(taskBottomSheetType = null)
            }
        }
    }

    private fun loadSortItems() {
        sortItemsJob?.cancel()
        sortItemsJob = launchIO {
            observeSortItemSelectedUseCase().collect(
                FlowCollector {
                    val sortItemSelected = it.getOrNull()
                    if (sortItemSelected != null) {
                        withMainContext {
                            _uiState.value = uiState.value.copy(
                                selectedSortItem = sortItemSelected
                            )
                        }
                    }
                    loadTasks()
                }
            )
        }
    }

    private fun loadTasks() {
        launchIO {
            getTasksFromSelectedChecklistUseCase(checkNotNull(checklistUuidReference))
                .onSuccess { tasksFromSelectedChecklist ->
                    sortTasksBySelectedSort(
                        uiState.value.selectedSortItem,
                        tasksFromSelectedChecklist
                    )
                }.onFailure {
                    emitLoadTasksFailure()
                }
        }
    }

    private suspend fun sortTasksBySelectedSort(
        selectedSortItem: SortItemType,
        tasks: List<Task>
    ) = sortTasksBySelectedSortUseCase(
        SortTasksBySelectedSortUseCase.Params(
            selectedSortItem,
            tasks
        )
    ).onSuccess { items ->
        val hasNoSelectedSort = selectedSortItem == SortItemType.UNSELECTED_SORT
        reverseTasksIfNeeded(
            shouldReverse = hasNoSelectedSort,
            tasks = items
        )
    }.onFailure {
        emitLoadTasksFailure()
    }

    private suspend fun reverseTasksIfNeeded(
        shouldReverse: Boolean,
        tasks: List<BaseTaskListItem>
    ) = reverseTasksIfNeededUseCase(
        ReverseTasksIfNeededUseCase.Params(
            shouldReverseList = shouldReverse,
            tasks = tasks
        )
    ).onSuccess { items ->
        withContext(main()) {
            _uiState.update { it.copy(tasks = items) }
        }
    }.onFailure {
        emitLoadTasksFailure()
    }

    private suspend fun emitLoadTasksFailure() {
        _uiEffect.emit(OnError(stringRes = R.string.task_item_component_load_tasks_error))
    }

    private fun onSortTaskClicked() {
        launchMain {
            _uiState.value = uiState.value.copy(taskBottomSheetType = TaskBottomSheetType.SORT_TASK_LIST)
            _uiEffect.emit(TaskComponentViewModelUiEffect.OpenBottomSheet)
        }
    }

    private fun onCompletableCountClicked() {
        launchMain {
            _uiState.value = uiState.value.copy(taskBottomSheetType = TaskBottomSheetType.COMPLETABLE_COUNT)
            _uiEffect.emit(TaskComponentViewModelUiEffect.OpenBottomSheet)
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
        _uiState.value = _uiState.value.copy(
            taskName = ""
        )
        addTaskToDatabaseUseCase(newTask).onSuccess {
            loadTasks()
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
                loadTasks()
            }.onFailure {
                _uiEffect.emit(OnError(stringRes = R.string.checklist_change_task_state_failure))
            }
        }
    }

    private fun handleDeleteTask(task: Task) {
        launchIO {
            getDeleteTaskUseCase(task).onSuccess {
                loadTasks()
            }
        }
    }
}

