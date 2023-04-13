package wottrich.github.io.androidsmartchecklist.presentation.viewmodel

import androidx.annotation.StringRes
import github.io.wottrich.checklist.domain.usecase.DeleteChecklistUseCase
import github.io.wottrich.checklist.domain.usecase.GetSelectedChecklistUseCase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.androidsmartchecklist.R
import wottrich.github.io.datasource.entity.NewChecklistWithNewTasks
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.quicklychecklist.impl.domain.ConvertChecklistIntoQuicklyChecklistUseCase
import wottrich.github.io.quicklychecklist.impl.domain.GetQuicklyChecklistDeepLinkUseCase
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
    private val convertChecklistIntoQuicklyChecklistUseCase: ConvertChecklistIntoQuicklyChecklistUseCase,
    private val getQuicklyChecklistDeepLinkUseCase: GetQuicklyChecklistDeepLinkUseCase
) : BaseViewModel(dispatchers) {

    private val pendingActions = MutableSharedFlow<HomeUiActions>()

    private val _homeStateFlow = MutableStateFlow(HomeState.Initial)
    val homeStateFlow = _homeStateFlow.asStateFlow()

    private val _uiEffects = SingleShotEventBus<HomeUiEffects>()
    val uiEffects: Flow<HomeUiEffects> = _uiEffects.events

    init {
        launchIO {
            getSelectedChecklistUseCase().collect(
                FlowCollector { selectedChecklistResult ->
                    val selectedChecklist = selectedChecklistResult.getOrNull()
                    handleSelectedChecklist(selectedChecklist)
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

    fun onShowTaskChangeStatusSnackbar(task: NewTask) {
        launchIO {
            pendingActions.emit(HomeUiActions.OnShowTaskChangeStatusSnackbar(task))
        }
    }

    fun onDeleteChecklist() {
        launchIO {
            pendingActions.emit(HomeUiActions.DeleteChecklistAction)
        }
    }

    fun onSnackbarError(@StringRes message: Int) {
        launchIO {
            _uiEffects.emit(HomeUiEffects.SnackbarError(message))
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
            is HomeUiActions.OnShowTaskChangeStatusSnackbar -> handleUpdateTaskAction(action.task)
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

    private suspend fun handleUpdateTaskAction(task: NewTask) {
        handleUpdateTaskEffect(task)
    }

    private suspend fun handleUpdateTaskEffect(task: NewTask) {
        val effect = if (task.isCompleted) {
            HomeUiEffects.SnackbarTaskUncompleted(task.name)
        } else {
            HomeUiEffects.SnackbarTaskCompleted(task.name)
        }
        _uiEffects.emit(effect)
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
                    handleQuicklyChecklistDeepLink(it)
                }.onFailure {
                    launchMain {
                        _uiEffects.emit(HomeUiEffects.SnackbarError(R.string.quickly_checklist_share_error))
                    }
                }
            }
        }
    }

    private fun handleQuicklyChecklistDeepLink(quicklyChecklistJson: String) {
        launchIO {
            getQuicklyChecklistDeepLinkUseCase(quicklyChecklistJson).onSuccess {
                _uiEffects.emit(HomeUiEffects.OnShareQuicklyChecklist(it))
            }.onFailure {
                _uiEffects.emit(HomeUiEffects.SnackbarError(R.string.quickly_checklist_share_error))
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
    data class OnShowTaskChangeStatusSnackbar(val task: NewTask) : HomeUiActions()
    object DeleteChecklistAction : HomeUiActions()
}