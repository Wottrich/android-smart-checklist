package wottrich.github.io.androidsmartchecklist.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import wottrich.github.io.database.entity.ChecklistWithTasks
import wottrich.github.io.database.entity.Task
import wottrich.github.io.featurenew.domain.usecase.AddTaskUseCase
import wottrich.github.io.featurenew.domain.usecase.DeleteTaskUseCase
import wottrich.github.io.featurenew.domain.usecase.GetDeleteChecklistUseCase
import wottrich.github.io.featurenew.domain.usecase.GetSelectedChecklistUseCase
import wottrich.github.io.featurenew.domain.usecase.GetUpdateSelectedChecklistUseCase
import wottrich.github.io.featurenew.domain.usecase.UpdateTaskUseCase
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
    private val getAddTaskUseCase: AddTaskUseCase,
    private val getUpdateTaskUseCase: UpdateTaskUseCase,
    private val getDeleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _homeStateFlow = MutableStateFlow(HomeState())
    val homeStateFlow = _homeStateFlow.asStateFlow()

    init {
        viewModelScope.launch(dispatchers.main) {
            getSelectedChecklistUseCase().collect { selectedChecklist ->
                handleSelectedChecklist(selectedChecklist)
            }
        }
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
            getUpdateTaskUseCase(task)
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
                //TODO change to next checklist or empty state
            }
        }
    }

    fun onChecklistClicked(checklistWithTasks: ChecklistWithTasks) {
        viewModelScope.launch(dispatchers.io) {
            _homeStateFlow.value = homeStateFlow.value.copy(isLoading = true)
            getUpdateSelectedChecklistUseCase(checklistWithTasks.checklist)
            _homeStateFlow.value =
                homeStateFlow.value.copy(
                    isLoading = false,
                    homeViewState = HomeViewState.Overview,
                    checklistWithTasks = checklistWithTasks
                )
        }
    }

    private fun handleSelectedChecklist(selectedChecklist: ChecklistWithTasks?) {
        _homeStateFlow.value =
            homeStateFlow.value.copy(isLoading = false, checklistWithTasks = selectedChecklist)
    }

}

data class HomeState(
    val isLoading: Boolean = true,
    val homeViewState: HomeViewState = HomeViewState.Overview,
    val checklistWithTasks: ChecklistWithTasks? = null
)

sealed class HomeViewState {
    object Overview : HomeViewState()
    object Edit : HomeViewState()
    object Delete : HomeViewState()
}