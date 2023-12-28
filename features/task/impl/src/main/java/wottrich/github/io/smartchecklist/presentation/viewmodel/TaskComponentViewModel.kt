package wottrich.github.io.smartchecklist.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
import wottrich.github.io.smartchecklist.domain.usecase.ReverseTasksIfNeededUseCase
import wottrich.github.io.smartchecklist.domain.usecase.SortTasksBySelectedSortUseCase
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction.Action
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction.Action.AddTask
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction.Action.ChangeTaskStatus
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction.Action.DeleteTask
import wottrich.github.io.smartchecklist.presentation.sort.model.TaskSortItemState
import wottrich.github.io.smartchecklist.presentation.state.TaskComponentUiState
import wottrich.github.io.smartchecklist.presentation.task.model.BaseTaskListItem
import wottrich.github.io.smartchecklist.presentation.viewmodel.TaskComponentViewModelUiEffect.OnError
import wottrich.github.io.smartchecklist.task.R

@OptIn(InternalCoroutinesApi::class)
class TaskComponentViewModel(
    private val observeSelectedChecklistUuidUseCase: ObserveSelectedChecklistUuidUseCase,
    private val getTasksFromSelectedChecklistUseCase: GetTasksFromSelectedChecklistUseCase,
    private val addTaskToDatabaseUseCase: AddTaskToDatabaseUseCase,
    private val getChangeTaskStatusUseCase: GetChangeTaskStatusUseCase,
    private val getDeleteTaskUseCase: GetDeleteTaskUseCase,
    private val sortTasksBySelectedSortUseCase: SortTasksBySelectedSortUseCase,
    private val reverseTasksIfNeededUseCase: ReverseTasksIfNeededUseCase
) : BaseViewModel(), TaskComponentViewModelAction {

    private var checklistUuidReference: String? = null

    var tasks = mutableStateListOf<BaseTaskListItem>()
        private set

    private val _uiState = MutableStateFlow(TaskComponentUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = SingleShotEventBus<TaskComponentViewModelUiEffect>()
    val uiEffect: Flow<TaskComponentViewModelUiEffect> = _uiEffect.events

    init {
        _uiState.value = uiState.value.copy(
            sortItems = buildSortItems()
        )
        launchIO {
            observeSelectedChecklistUuidUseCase().collect(
                FlowCollector {
                    checklistUuidReference = it.getOrNull()
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
                        uiState.value.sortItems,
                        tasksFromSelectedChecklist
                    )
                }.onFailure {
                    emitLoadTasksFailure()
                }
        }
    }

    private suspend fun sortTasksBySelectedSort(
        sortItems: List<TaskSortItemState>,
        tasks: List<Task>
    ) = sortTasksBySelectedSortUseCase(
        SortTasksBySelectedSortUseCase.Params(
            sortItems,
            tasks
        )
    ).onSuccess { items ->
        val selectedSortItem = sortItems.firstOrNull { it.isSelected }?.type
        val hasNoSelectedSort = selectedSortItem == null
        reverseTasksIfNeeded(
            shouldReverse = hasNoSelectedSort || selectedSortItem == SortItemType.UNSELECTED_SORT,
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
        this.tasks.clear()
        this.tasks.addAll(items)
    }.onFailure {
        emitLoadTasksFailure()
    }

    private suspend fun emitLoadTasksFailure() {
        _uiEffect.emit(OnError(stringRes = R.string.task_item_component_load_tasks_error))
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

            is Action.OnSortItemClicked -> {
                changeSelectedSortItem(action.sortItemState)
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

    private fun changeSelectedSortItem(selectedSortItem: TaskSortItemState) {
        val isSelectedTheSame =
            uiState.value.sortItems.firstOrNull { it.isSelected } == selectedSortItem
        val sortItemTypeBySelectedItem =
            if (isSelectedTheSame) SortItemType.UNSELECTED_SORT else selectedSortItem.type
        _uiState.update {
            val newItems = buildSortItems(sortItemTypeBySelectedItem)
            it.copy(sortItems = newItems)
        }.also {
            loadTasks()
        }
    }

    private fun buildSortItems(selectedSortItem: SortItemType? = SortItemType.UNSELECTED_SORT): List<TaskSortItemState> {
        return listOf(
            TaskSortItemState(
                SortItemType.UNCOMPLETED_TASKS,
                R.string.task_sort_uncompleted_task_item,
                selectedSortItem == SortItemType.UNCOMPLETED_TASKS
            ),
            TaskSortItemState(
                SortItemType.COMPLETED_TASKS,
                R.string.task_sort_completed_task_item,
                selectedSortItem == SortItemType.COMPLETED_TASKS
            )
        )
    }
}

sealed class TaskComponentViewModelUiEffect {
    data class OnError(@StringRes val stringRes: Int) : TaskComponentViewModelUiEffect()
}