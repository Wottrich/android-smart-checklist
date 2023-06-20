package wottrich.github.io.androidsmartchecklist.presentation.viewmodel

import androidx.annotation.StringRes
import github.io.wottrich.checklist.domain.usecase.DeleteChecklistUseCase
import github.io.wottrich.checklist.domain.usecase.GetSelectedChecklistUseCase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.androidsmartchecklist.R
import wottrich.github.io.androidsmartchecklist.presentation.state.HomeState
import wottrich.github.io.androidsmartchecklist.presentation.state.HomeUiActions
import wottrich.github.io.androidsmartchecklist.presentation.state.HomeUiActions.Action
import wottrich.github.io.androidsmartchecklist.presentation.state.HomeUiActions.Action.DeleteChecklistAction
import wottrich.github.io.androidsmartchecklist.presentation.state.HomeUiActions.Action.OnChangeEditModeAction
import wottrich.github.io.androidsmartchecklist.presentation.state.HomeUiActions.Action.OnShareQuicklyChecklistAction
import wottrich.github.io.androidsmartchecklist.presentation.state.HomeUiActions.Action.OnShowTaskChangeStatusSnackbar
import wottrich.github.io.androidsmartchecklist.presentation.state.HomeUiActions.Action.OnSnackbarError
import wottrich.github.io.androidsmartchecklist.presentation.state.HomeUiEffects
import wottrich.github.io.androidsmartchecklist.presentation.state.HomeUiState
import wottrich.github.io.datasource.entity.NewChecklistWithNewTasks
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.quicklychecklist.impl.domain.ConvertChecklistIntoQuicklyChecklistUseCase
import wottrich.github.io.quicklychecklist.impl.domain.GetQuicklyChecklistDeepLinkUseCase
import wottrich.github.io.tools.SingleShotEventBus
import wottrich.github.io.tools.base.BaseViewModel
import github.io.wottrich.coroutines.base.onFailure
import github.io.wottrich.coroutines.base.onSuccess
import github.io.wottrich.coroutines.dispatcher.DispatchersProviders

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
) : BaseViewModel(dispatchers), HomeUiActions {

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
    }

    override fun sendAction(action: Action) {
        when (action) {
            DeleteChecklistAction -> onDeleteChecklistAction()
            OnChangeEditModeAction -> onChangeEditModeClicked()
            is OnShowTaskChangeStatusSnackbar -> onShowTaskChangeStatusSnackbar(action.task)
            OnShareQuicklyChecklistAction -> onShareQuicklyChecklist()
            is OnSnackbarError -> onSnackbarError(action.message)
        }
    }

    private fun onDeleteChecklistAction() {
        launchIO {
            homeStateFlow.value.checklistWithTasks?.newChecklist?.let {
                deleteChecklistUseCase(it)
                _uiEffects.emit(HomeUiEffects.SnackbarChecklistDelete)
            }
        }
    }

    private fun onChangeEditModeClicked() {
        if (homeStateFlow.value.isEditUiState) {
            handleDisableEditMode()
        } else {
            handleEnabledEditMode()
        }
    }

    private fun onShowTaskChangeStatusSnackbar(task: NewTask) {
        launchIO {
            handleUpdateTaskEffect(task)
        }
    }

    private fun onShareQuicklyChecklist() {
        val checklistWithTasks = homeStateFlow.value.checklistWithTasks
        if (checklistWithTasks != null) {
            launchIO {
                convertChecklistIntoQuicklyChecklistUseCase(checklistWithTasks).onSuccess {
                    handleQuicklyChecklistDeepLink(it)
                }.onFailure {
                    _uiEffects.emit(HomeUiEffects.SnackbarError(R.string.quickly_checklist_share_error))
                }
            }
        }
    }

    private fun onSnackbarError(@StringRes message: Int) {
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

    private fun handleEnabledEditMode() {
        val state = HomeUiState.Overview(isEditing = true)
        _homeStateFlow.value = homeStateFlow.value.copy(homeUiState = state)
    }

    private fun handleDisableEditMode() {
        val state = HomeUiState.Overview(isEditing = false)
        _homeStateFlow.value = homeStateFlow.value.copy(homeUiState = state)
    }

    private suspend fun handleUpdateTaskEffect(task: NewTask) {
        val effect = if (task.isCompleted) {
            HomeUiEffects.SnackbarTaskUncompleted(task.name)
        } else {
            HomeUiEffects.SnackbarTaskCompleted(task.name)
        }
        _uiEffects.emit(effect)
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