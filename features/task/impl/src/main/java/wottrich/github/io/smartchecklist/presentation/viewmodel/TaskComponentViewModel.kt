package wottrich.github.io.smartchecklist.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.datasource.entity.NewTask
import wottrich.github.io.smartchecklist.domain.model.SortItemType
import wottrich.github.io.smartchecklist.domain.usecase.AddTaskToDatabaseUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetChangeTaskStatusUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetDeleteTaskUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetTasksFromSelectedChecklistUseCase
import wottrich.github.io.smartchecklist.domain.usecase.SortTasksBySelectedSortUseCase
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction.Action
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction.Action.AddTask
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction.Action.ChangeTaskStatus
import wottrich.github.io.smartchecklist.presentation.action.TaskComponentViewModelAction.Action.DeleteTask
import wottrich.github.io.smartchecklist.presentation.sort.model.TaskSortItemState
import wottrich.github.io.smartchecklist.presentation.state.TaskComponentUiState
import wottrich.github.io.smartchecklist.presentation.viewmodel.TaskComponentViewModelUiEffect.OnError
import wottrich.github.io.smartchecklist.task.R
import java.util.concurrent.CancellationException

@OptIn(InternalCoroutinesApi::class)
class TaskComponentViewModel(
    private val getTasksFromSelectedChecklistUseCase: GetTasksFromSelectedChecklistUseCase,
    private val addTaskToDatabaseUseCase: AddTaskToDatabaseUseCase,
    private val getChangeTaskStatusUseCase: GetChangeTaskStatusUseCase,
    private val getDeleteTaskUseCase: GetDeleteTaskUseCase,
    private val sortTasksBySelectedSortUseCase: SortTasksBySelectedSortUseCase
) : BaseViewModel(), TaskComponentViewModelAction {

    private var observerTasksJob: Job? = null

    private var checklistUuidReference: String? = null

    var tasks = mutableStateListOf<NewTask>()
        private set

    private val _uiState = MutableStateFlow(TaskComponentUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = SingleShotEventBus<TaskComponentViewModelUiEffect>()
    val uiEffect: Flow<TaskComponentViewModelUiEffect> = _uiEffect.events

    init {
        _uiState.value = uiState.value.copy(
            sortItems = buildSortItems()
        )
        observeTasks()
    }

    private fun observeTasks() {
        observerTasksJob?.cancel(CancellationException())
        observerTasksJob = launchIO {
            getTasksFromSelectedChecklistUseCase().cancellable().collect(
                FlowCollector { result ->
                    val simpleModel = checkNotNull(result.getOrNull())
                    checklistUuidReference = simpleModel.parentUuid
                    sortTasksBySelectedSortUseCase(
                        SortTasksBySelectedSortUseCase.Params(
                            uiState.value.sortItems,
                            simpleModel.tasks
                        )
                    ).onSuccess {
                        withContext(main()) {
                            tasks.clear()
                            tasks.addAll(it)
                        }
                    }.onFailure {
                        _uiEffect.emit(OnError(stringRes = R.string.task_item_component_load_tasks_error))
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

    private fun changeSelectedSortItem(selectedSortItem: TaskSortItemState) {
        val isSelectedTheSame =
            uiState.value.sortItems.firstOrNull { it.isSelected } == selectedSortItem
        val sortItemTypeBySelectedItem =
            if (isSelectedTheSame) SortItemType.UNSELECTED_SORT else selectedSortItem.type
        _uiState.update {
            val newItems = buildSortItems(sortItemTypeBySelectedItem)
            it.copy(sortItems = newItems)
        }.also {
            observeTasks()
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