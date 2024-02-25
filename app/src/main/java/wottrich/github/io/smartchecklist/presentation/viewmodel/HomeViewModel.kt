package wottrich.github.io.smartchecklist.presentation.viewmodel

import androidx.annotation.StringRes
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.checklist.domain.DeleteChecklistUseCase
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.domain.usecase.ObserveSimpleSelectedChecklistModelUseCase
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus
import wottrich.github.io.smartchecklist.presentation.state.HomeState
import wottrich.github.io.smartchecklist.presentation.state.HomeUiActions
import wottrich.github.io.smartchecklist.presentation.state.HomeUiEffects
import wottrich.github.io.smartchecklist.presentation.state.HomeUiState
import wottrich.github.io.smartchecklist.presentation.ui.model.SimpleChecklistModel

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
    private val observeSimpleSelectedChecklistModelUseCase: ObserveSimpleSelectedChecklistModelUseCase,
    private val deleteChecklistUseCase: DeleteChecklistUseCase,
) : BaseViewModel(dispatchers), HomeUiActions {

    private val _homeStateFlow = MutableStateFlow(HomeState.Initial)
    val homeStateFlow = _homeStateFlow.asStateFlow()

    private val _uiEffects = SingleShotEventBus<HomeUiEffects>()
    val uiEffects: Flow<HomeUiEffects> = _uiEffects.events

    init {
        launchIO {
            observeSimpleSelectedChecklistModelUseCase().collect(
                FlowCollector { selectedChecklistResult ->
                    val selectedChecklist = selectedChecklistResult.getOrNull()
                    handleSelectedChecklist(selectedChecklist)
                }
            )
        }
    }

    override fun sendAction(action: HomeUiActions.Action) {
        when (action) {
            HomeUiActions.Action.DeleteChecklistAction -> onDeleteChecklistAction()
            HomeUiActions.Action.OnChangeEditModeAction -> onChangeEditModeClicked()
            is HomeUiActions.Action.OnShowTaskChangeStatusSnackbar -> onShowTaskChangeStatusSnackbar(
                action.task
            )

            is HomeUiActions.Action.OnSnackbarError -> onSnackbarError(action.message)
        }
    }

    private fun onDeleteChecklistAction() {
        launchIO {
            homeStateFlow.value.checklist?.uuid?.let {
                deleteChecklistUseCase(it).onSuccess {
                    _uiEffects.emit(HomeUiEffects.SnackbarChecklistDelete)
                }
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

    private fun onShowTaskChangeStatusSnackbar(task: Task) {
        launchIO {
            handleUpdateTaskEffect(task)
        }
    }

    private fun onSnackbarError(@StringRes message: Int) {
        launchIO {
            _uiEffects.emit(HomeUiEffects.SnackbarError(message))
        }
    }

    private fun handleSelectedChecklist(selectedChecklist: SimpleChecklistModel?) {
        val nextUiState = getNextUiState(selectedChecklist)
        _homeStateFlow.value = homeStateFlow.value.copy(
            homeUiState = nextUiState,
            checklist = selectedChecklist
        )
    }

    private fun getNextUiState(selectedChecklist: SimpleChecklistModel?): HomeUiState {
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

    private suspend fun handleUpdateTaskEffect(task: Task) {
        val effect = if (task.isCompleted) {
            HomeUiEffects.SnackbarTaskUncompleted(task.name)
        } else {
            HomeUiEffects.SnackbarTaskCompleted(task.name)
        }
        _uiEffects.emit(effect)
    }
}