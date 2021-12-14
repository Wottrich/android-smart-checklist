package wottrich.github.io.androidsmartchecklist.presentation.viewmodel

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
    private val getDeleteTaskUseCase: GetDeleteTaskUseCase
) : ViewModel() {

    private val _homeStateFlow = MutableStateFlow(HomeState())
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
        val state = if (homeStateFlow.value.isEditViewState) HomeViewState.Overview
        else HomeViewState.Edit
        onChangeState(state)
    }

    fun onChangeState(state: HomeViewState) {
        _homeStateFlow.value = homeStateFlow.value.copy(homeViewState = state)
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

    fun onChecklistClicked(checklistWithTasks: ChecklistWithTasks) {
        viewModelScope.launch(dispatchers.main) {
            if (!checklistWithTasks.checklist.isSelected) {
                _homeStateFlow.value =
                    homeStateFlow.value.copy(homeViewState = HomeViewState.Loading)
                getUpdateSelectedChecklistUseCase(checklistWithTasks.checklist)
            }
        }
    }

    private fun handleSelectedChecklist(selectedChecklist: ChecklistWithTasks?) {
        val currentViewState = homeStateFlow.value.homeViewState
        val nextViewState = if (currentViewState == HomeViewState.Loading) HomeViewState.Overview
        else currentViewState
        _homeStateFlow.value = homeStateFlow.value.copy(
            homeViewState = nextViewState,
            checklistWithTasks = selectedChecklist
        )
    }

}

data class HomeState(
    val homeViewState: HomeViewState = HomeViewState.Loading,
    val checklistWithTasks: ChecklistWithTasks? = null
) {
    val isEditViewState: Boolean
        get() = homeViewState == HomeViewState.Edit
}

sealed class HomeViewState {
    object Loading : HomeViewState()
    object Overview : HomeViewState()
    object Edit : HomeViewState()
}