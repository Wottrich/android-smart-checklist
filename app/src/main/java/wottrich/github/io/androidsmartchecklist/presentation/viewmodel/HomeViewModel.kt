package wottrich.github.io.androidsmartchecklist.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateListOf
import github.io.wottrich.checklist.domain.usecase.DeleteChecklistUseCase
import github.io.wottrich.checklist.domain.usecase.GetSelectedChecklistUseCase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import wottrich.github.io.androidsmartchecklist.R
import wottrich.github.io.datasource.entity.NewChecklistWithNewTasks
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.impl.domain.usecase.GetAddTaskUseCase
import wottrich.github.io.impl.domain.usecase.GetChangeTaskStatusUseCase
import wottrich.github.io.impl.domain.usecase.GetDeleteTaskUseCase
import wottrich.github.io.quicklychecklist.impl.domain.ConvertChecklistIntoQuicklyChecklistUseCase
import wottrich.github.io.tools.SingleShotEventBus
import wottrich.github.io.tools.base.BaseViewModel
import wottrich.github.io.tools.base.onFailure
import wottrich.github.io.tools.base.onSuccess
import wottrich.github.io.tools.dispatcher.DispatchersProviders

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 06/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@OptIn(InternalCoroutinesApi::class)
class HomeViewModel(
    dispatchers: DispatchersProviders,
    private val getSelectedChecklistUseCase: GetSelectedChecklistUseCase,
    private val deleteChecklistUseCase: DeleteChecklistUseCase,
    private val getAddTaskUseCase: GetAddTaskUseCase,
    private val getChangeTaskStatusUseCase: GetChangeTaskStatusUseCase,
    private val getDeleteTaskUseCase: GetDeleteTaskUseCase,
    private val convertChecklistIntoQuicklyChecklistUseCase: ConvertChecklistIntoQuicklyChecklistUseCase
) : BaseViewModel(dispatchers) {

    private val pendingActions = MutableSharedFlow<HomeUiActions>()

    private val _homeStateFlow = MutableStateFlow(HomeState.Initial)
    val homeStateFlow = _homeStateFlow.asStateFlow()

    private val _uiEffects = SingleShotEventBus<HomeUiEffects>()
    val uiEffects: Flow<HomeUiEffects> = _uiEffects.events

    var tasks = mutableStateListOf<NewTask>()
        private set

    init {
        launchIO {
            getSelectedChecklistUseCase().collect(
                FlowCollector { selectedChecklistResult ->
                    val selectedChecklist = selectedChecklistResult.getOrNull()
                    handleSelectedChecklist(selectedChecklist)
                    withContext(dispatchers.main) {
                        tasks.clear()
                        tasks.addAll(selectedChecklist?.newTasks.orEmpty())
                    }
                }
            )
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

    fun onUpdateItemClicked(task: NewTask) {
        launchIO {
            pendingActions.emit(HomeUiActions.UpdateTaskAction(task))
        }
    }

    fun onDeleteItemClicked(task: NewTask) {
        launchIO {
            pendingActions.emit(HomeUiActions.DeleteTaskAction(task))
        }
    }

    fun onDeleteChecklist() {
        launchIO {
            pendingActions.emit(HomeUiActions.DeleteChecklistAction)
        }
    }

    private fun handleSelectedChecklist(selectedChecklist: NewChecklistWithNewTasks?) {
        val nextUiState = getNextUiState(selectedChecklist)
        _homeStateFlow.value = homeStateFlow.value.copy(
            homeUiState = nextUiState,
            checklistWithTasks = selectedChecklist
        )
    }

    private fun getNextUiState(selectedChecklist: NewChecklistWithNewTasks?): HomeUiState {
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
        val checklistId = homeStateFlow.value.checklistWithTasks?.newChecklist?.uuid
        if (checklistId != null) {
            getAddTaskUseCase(
                GetAddTaskUseCase.Params(
                    parentUuid = checklistId,
                    taskName = taskName
                )
            )
        }
    }

    private suspend fun handleUpdateTaskAction(task: NewTask) {
        handleUpdateTaskEffect(task)
        getChangeTaskStatusUseCase(task)
    }

    private suspend fun handleUpdateTaskEffect(task: NewTask) {
        val effect = if (task.isCompleted) {
            HomeUiEffects.SnackbarTaskUncompleted(task.name)
        } else {
            HomeUiEffects.SnackbarTaskCompleted(task.name)
        }
        _uiEffects.emit(effect)
    }

    private fun handleDeleteTaskAction(task: NewTask) {
        launchIO {
            getDeleteTaskUseCase(task)
        }
    }

    private fun handleDeleteChecklistAction() {
        launchIO {
            homeStateFlow.value.checklistWithTasks?.newChecklist?.let {
                deleteChecklistUseCase(it)
                _uiEffects.emit(HomeUiEffects.SnackbarChecklistDelete)
            }
        }
    }

    fun onShareQuicklyChecklist() {
        val checklistWithTasks = homeStateFlow.value.checklistWithTasks
        if (checklistWithTasks != null) {
            launchIO {
                convertChecklistIntoQuicklyChecklistUseCase(checklistWithTasks).onSuccess {
                    launchMain {
                        _uiEffects.emit(HomeUiEffects.OnShareQuicklyChecklist(it))
                    }
                }.onFailure {
                    launchMain {
                        _uiEffects.emit(HomeUiEffects.SnackbarError(R.string.quickly_checklist_share_error))
                    }
                }
            }
        }
    }

}

data class HomeState(
    val homeUiState: HomeUiState,
    val checklistWithTasks: NewChecklistWithNewTasks?
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
    data class SnackbarError(@StringRes val errorMessage: Int) : HomeUiEffects()
    data class OnShareQuicklyChecklist(val quicklyChecklistJson: String) : HomeUiEffects()
}

sealed class HomeUiActions {
    object EnableEditModeAction : HomeUiActions()
    object DisableEditModeAction : HomeUiActions()
    data class AddNewTaskAction(val taskName: String) : HomeUiActions()
    data class UpdateTaskAction(val task: NewTask) : HomeUiActions()
    data class DeleteTaskAction(val task: NewTask) : HomeUiActions()
    object DeleteChecklistAction : HomeUiActions()
}