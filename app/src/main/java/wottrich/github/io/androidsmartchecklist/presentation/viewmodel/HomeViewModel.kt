package wottrich.github.io.androidsmartchecklist.presentation.viewmodel

import android.content.ClipData
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.io.wottrich.checklist.domain.usecase.GetDeleteChecklistUseCase
import github.io.wottrich.checklist.domain.usecase.GetSelectedChecklistUseCase
import github.io.wottrich.checklist.domain.usecase.GetUpdateSelectedChecklistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wottrich.github.io.database.entity.ChecklistWithTasks
import wottrich.github.io.database.entity.Task
import wottrich.github.io.publicandroid.domain.usecase.GetAddTaskUseCase
import wottrich.github.io.publicandroid.domain.usecase.GetChangeTaskStatusUseCase
import wottrich.github.io.publicandroid.domain.usecase.GetDeleteTaskUseCase
import wottrich.github.io.tools.dispatcher.DispatchersProviders
import wottrich.github.io.tools.utils.Clipboard

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 06/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

class HomeViewModel(
    private val dispatchers: DispatchersProviders,
    private val getUpdateSelectedChecklistUseCase: GetUpdateSelectedChecklistUseCase,
    private val getSelectedChecklistUseCase: GetSelectedChecklistUseCase,
    private val getDeleteChecklistUseCase: GetDeleteChecklistUseCase,
    private val getAddTaskUseCase: GetAddTaskUseCase,
    private val getChangeTaskStatusUseCase: GetChangeTaskStatusUseCase,
    private val getDeleteTaskUseCase: GetDeleteTaskUseCase,
    private val clipboard: Clipboard
) : ViewModel() {

    private val _homeStateFlow = MutableStateFlow(HomeState.Initial)
    val homeStateFlow = _homeStateFlow.asStateFlow()

    var tasks = mutableStateListOf<Task>()
        private set

    init {
        viewModelScope.launch(dispatchers.io) {
            getSelectedChecklistUseCase().collect { selectedChecklist ->
                handleSelectedChecklist(selectedChecklist)
                withContext(dispatchers.main) {
                    tasks.clear()
                    tasks.addAll(selectedChecklist?.tasks.orEmpty())
                }
            }
        }
    }

    fun onChangeEditModeClicked() {
        onChangeState(HomeUiState.Overview(isEditing = !homeStateFlow.value.isEditUiState))
    }

    fun onChangeState(state: HomeUiState) {
        _homeStateFlow.value = homeStateFlow.value.copy(homeUiState = state)
    }

    fun onAddItemClicked(taskName: String) {
        val checklistId = homeStateFlow.value.checklistWithTasks?.checklist?.checklistId
        checklistId?.let {
            viewModelScope.launch(dispatchers.io) {
                getAddTaskUseCase(it, taskName)
            }
        }
    }

    fun onUpdateItemClicked(task: Task) {
        viewModelScope.launch(dispatchers.io) {
            getChangeTaskStatusUseCase(task)
        }
    }

    fun onDeleteItemClicked(task: Task) {
        viewModelScope.launch(dispatchers.io) {
            getDeleteTaskUseCase(task)
        }
    }

    fun onDeleteChecklist() {
        viewModelScope.launch(dispatchers.io) {
            homeStateFlow.value.checklistWithTasks?.checklist?.let {
                getDeleteChecklistUseCase(it)
            }
        }
    }

    fun onShareChecklist() {
        homeStateFlow.value.checklistWithTasks?.let {
            val checklistName = it.checklist.name
            val clipData = ClipData.newPlainText(checklistName, it.toString())
            clipboard.copy(clipData)
        }
    }

    private fun handleSelectedChecklist(selectedChecklist: ChecklistWithTasks?) {
        val currentViewState = homeStateFlow.value.homeUiState
        val hasSelectedChecklist = selectedChecklist != null
        val nextUiState = when {
            shouldVerifyUiStateToUpdate(currentViewState) -> getNextUiState(hasSelectedChecklist)
            hasSelectedChecklist -> currentViewState
            else -> HomeUiState.Empty
        }
        _homeStateFlow.value = homeStateFlow.value.copy(
            homeUiState = nextUiState,
            checklistWithTasks = selectedChecklist
        )
    }

    private fun shouldVerifyUiStateToUpdate(currentUiState: HomeUiState): Boolean {
        return currentUiState == HomeUiState.Loading || currentUiState == HomeUiState.Empty
    }

    private fun getNextUiState(hasSelectedChecklist: Boolean) =
        if (hasSelectedChecklist) HomeUiState.Overview(false) else HomeUiState.Empty

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