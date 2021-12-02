package wottrich.github.io.androidsmartchecklist.view

import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import wottrich.github.io.database.dao.ChecklistDao
import wottrich.github.io.database.entity.ChecklistWithTasks
import wottrich.github.io.tools.dispatcher.DispatchersProviders

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 12/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

class DrawerViewModel(
    private val dispatchers: DispatchersProviders,
    private val dao: ChecklistDao
) : ViewModel() {

    private val _drawerStateFlow = MutableStateFlow(HomeDrawerState())
    val drawerStateFlow: StateFlow<HomeDrawerState> = _drawerStateFlow

    private val _homeStateFlow = MutableStateFlow(HomeState())
    val homeStateFlow = _homeStateFlow.asStateFlow()

    init {
        viewModelScope.launch(dispatchers.io) {
            dao.selectAllChecklistWithTasks().collect {
                _drawerStateFlow.value =
                    drawerStateFlow.value.copy(isLoading = false, checklists = it)
                handleSelectedChecklist(it)
            }
        }
    }

    fun onChecklistClicked(checklistWithTasks: ChecklistWithTasks) {
        viewModelScope.launch(dispatchers.io) {
            removeLastSelectedChecklist()
            val checklist = checklistWithTasks.checklist
            checklist.isSelected = true
            dao.update(checklist)
            _homeStateFlow.value =
                homeStateFlow.value.copy(isLoading = false, checklistWithTasks = checklistWithTasks)
        }
    }

    private suspend fun removeLastSelectedChecklist() {
        val currentSelectedChecklist = homeStateFlow.value.checklistWithTasks?.checklist
        currentSelectedChecklist?.isSelected = false
        currentSelectedChecklist?.let {
            dao.update(it)
        }
    }

    private suspend fun handleSelectedChecklist(checklistWithTasksList: List<ChecklistWithTasks>) {
        val selectedChecklist: ChecklistWithTasks? = checklistWithTasksList.firstOrNull {
            it.checklist.isSelected
        }
        if (selectedChecklist == null) {
            val firstChecklist = checklistWithTasksList.firstOrNull()
            if (firstChecklist != null) {
                val checklist = firstChecklist.checklist
                checklist.isSelected = true
                dao.update(checklist)
                _homeStateFlow.value = homeStateFlow.value.copy(
                    isLoading = false,
                    checklistWithTasks = firstChecklist
                )
            } else {
                _homeStateFlow.value = homeStateFlow.value.copy(isLoading = false)
            }
        } else {
            _homeStateFlow.value = homeStateFlow.value.copy(
                isLoading = false,
                checklistWithTasks = selectedChecklist
            )
        }
    }

}

data class HomeDrawerState(
    val isLoading: Boolean = true,
    val checklists: List<ChecklistWithTasks> = emptyList()
)

data class HomeState(
    val isLoading: Boolean = true,
    val checklistWithTasks: ChecklistWithTasks? = null
)