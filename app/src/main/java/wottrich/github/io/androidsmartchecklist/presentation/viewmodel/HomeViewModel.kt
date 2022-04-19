package wottrich.github.io.androidsmartchecklist.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import github.io.wottrich.checklist.domain.usecase.DeleteChecklistUseCase
import github.io.wottrich.checklist.domain.usecase.GetSelectedChecklistUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import wottrich.github.io.datasource.entity.ChecklistWithTasks
import wottrich.github.io.datasource.entity.Task
import wottrich.github.io.impl.domain.usecase.GetAddTaskUseCase
import wottrich.github.io.impl.domain.usecase.GetChangeTaskStatusUseCase
import wottrich.github.io.impl.domain.usecase.GetDeleteTaskUseCase
import wottrich.github.io.tools.SingleShotEventBus
import wottrich.github.io.tools.base.BaseViewModel
import wottrich.github.io.tools.dispatcher.DispatchersProviders

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 06/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

class HomeViewModel(
    dispatchers: DispatchersProviders,
    private val getSelectedChecklistUseCase: GetSelectedChecklistUseCase,
    private val deleteChecklistUseCase: DeleteChecklistUseCase,
    private val getAddTaskUseCase: GetAddTaskUseCase,
    private val getChangeTaskStatusUseCase: GetChangeTaskStatusUseCase,
    private val getDeleteTaskUseCase: GetDeleteTaskUseCase,
) : BaseViewModel(dispatchers) {

    private val pendingActions = MutableSharedFlow<HomeUiActions>()

    private val _homeStateFlow = MutableStateFlow(HomeState.Initial)
    val homeStateFlow = _homeStateFlow.asStateFlow()

    private val _uiEffects = SingleShotEventBus<HomeUiEffects>()
    val uiEffects: Flow<HomeUiEffects> = _uiEffects.events

    var tasks = mutableStateListOf<Task>()
        private set

    init {
        launchIO {
            getSelectedChecklistUseCase().collect { selectedChecklist ->
                handleSelectedChecklist(selectedChecklist)
                withContext(dispatchers.main) {
                    tasks.clear()
                    tasks.addAll(selectedChecklist?.tasks.orEmpty())
                }
            }
        }

        launchIO {
            pendingActions.collect { handleActions(it) }
        }
    }

    fun onChangeEditModeClicked() {
        launchIO {
            if (homeStateFlow.value.isEditUiState) {
                pendingActions.emit(HomeUiActions.DisableEditModeAction)
            } else {
                pendingActions.emit(HomeUiActions.EnableEditModeAction)
            }
        }
    }

    fun onAddItemButtonClicked(taskName: String) {
        launchIO {
            pendingActions.emit(HomeUiActions.AddNewTaskAction(taskName))
        }
    }

    fun onUpdateItemClicked(task: Task) {
        launchIO {
            pendingActions.emit(HomeUiActions.UpdateTaskAction(task))
        }
    }

    fun onDeleteItemClicked(task: Task) {
        launchIO {
            pendingActions.emit(HomeUiActions.DeleteTaskAction(task))
        }
    }

    fun onDeleteChecklist() {
        launchIO {
            pendingActions.emit(HomeUiActions.DeleteChecklistAction)
        }
    }

    private fun handleSelectedChecklist(selectedChecklist: ChecklistWithTasks?) {
        val nextUiState = getNextUiState(selectedChecklist)
        _homeStateFlow.value = homeStateFlow.value.copy(
            homeUiState = nextUiState,
            checklistWithTasks = selectedChecklist
        )
    }

    private fun getNextUiState(selectedChecklist: ChecklistWithTasks?): HomeUiState {
        val currentViewState = homeStateFlow.value.homeUiState
        val hasSelectedChecklist = selectedChecklist != null
        return when {
            shouldVerifyUiStateToUpdate(currentViewState) ->
                getNextUiStateBySelectedState(hasSelectedChecklist)
            hasSelectedChecklist -> currentViewState
            else -> HomeUiState.Empty
        }
    }

    private fun shouldVerifyUiStateToUpdate(currentUiState: HomeUiState): Boolean {
        return currentUiState == HomeUiState.Loading || currentUiState == HomeUiState.Empty
    }

    private fun getNextUiStateBySelectedState(hasSelectedChecklist: Boolean) =
        if (hasSelectedChecklist) HomeUiState.Overview(false) else HomeUiState.Empty

    private suspend fun handleActions(action: HomeUiActions) {
        when (action) {
            HomeUiActions.DisableEditModeAction -> handleDisableEditMode()
            HomeUiActions.EnableEditModeAction -> handleEnabledEditMode()
            is HomeUiActions.AddNewTaskAction -> handleAddNewTaskAction(action.taskName)
            is HomeUiActions.UpdateTaskAction -> handleUpdateTaskAction(action.task)
            is HomeUiActions.DeleteTaskAction -> handleDeleteTaskAction(action.task)
            HomeUiActions.DeleteChecklistAction -> handleDeleteChecklistAction()
        }
    }

    private fun handleEnabledEditMode() {
        val state = HomeUiState.Overview(isEditing = true)
        _homeStateFlow.value = homeStateFlow.value.copy(homeUiState = state)
    }

    private fun handleDisableEditMode() {
        val state = HomeUiState.Overview(isEditing = false)
        _homeStateFlow.value = homeStateFlow.value.copy(homeUiState = state)
    }

    private suspend fun handleAddNewTaskAction(taskName: String) {
        val checklistId = homeStateFlow.value.checklistWithTasks?.checklist?.checklistId
        checklistId?.let {
            getAddTaskUseCase(it, taskName)
        }
    }

    private suspend fun handleUpdateTaskAction(task: Task) {
        handleUpdateTaskEffect(task)
        getChangeTaskStatusUseCase(task)
    }

    private suspend fun handleUpdateTaskEffect(task: Task) {
        val effect = if (task.isCompleted) {
            HomeUiEffects.SnackbarTaskUncompleted(task.name)
        } else {
            HomeUiEffects.SnackbarTaskCompleted(task.name)
        }
        _uiEffects.emit(effect)
    }

    private fun handleDeleteTaskAction(task: Task) {
        launchIO {
            getDeleteTaskUseCase(task)
        }
    }

    private fun handleDeleteChecklistAction() {
        launchIO {
            homeStateFlow.value.checklistWithTasks?.checklist?.let {
                deleteChecklistUseCase(it)
                _uiEffects.emit(HomeUiEffects.SnackbarChecklistDelete)
            }
        }
    }

}

data class HomeState(
    val homeUiState: HomeUiState,
    val checklistWithTasks: ChecklistWithTasks?
) {
    val isEditUiState: Boolean
        get() = homeUiState is HomeUiState.Overview && homeUiState.isEditing

    fun shouldShowActionContent(): Boolean {
        return homeUiState is HomeUiState.Overview
    }

    companion object {
        val Initial = HomeState(HomeUiState.Loading, null)
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Overview(val isEditing: Boolean = false) : HomeUiState()
    object Empty : HomeUiState()
}

sealed class HomeUiEffects {
    data class SnackbarTaskCompleted(val taskName: String) : HomeUiEffects()
    data class SnackbarTaskUncompleted(val taskName: String) : HomeUiEffects()
    object SnackbarChecklistDelete : HomeUiEffects()
}

sealed class HomeUiActions {
    object EnableEditModeAction : HomeUiActions()
    object DisableEditModeAction : HomeUiActions()
    data class AddNewTaskAction(val taskName: String) : HomeUiActions()
    data class UpdateTaskAction(val task: Task) : HomeUiActions()
    data class DeleteTaskAction(val task: Task) : HomeUiActions()
    object DeleteChecklistAction : HomeUiActions()
}