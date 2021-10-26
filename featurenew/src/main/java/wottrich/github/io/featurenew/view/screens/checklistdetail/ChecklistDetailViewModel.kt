package wottrich.github.io.featurenew.view.screens.checklistdetail

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import wottrich.github.io.database.dao.ChecklistDao
import wottrich.github.io.database.dao.TaskDao
import wottrich.github.io.featurenew.view.screens.tasklist.TaskListViewModel
import wottrich.github.io.tools.dispatcher.DispatchersProviders

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 21/10/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

class ChecklistDetailViewModel(
    taskDao: TaskDao,
    private val checklistId: String,
    private val dispatchersProviders: DispatchersProviders,
    private val checklistDao: ChecklistDao
) : TaskListViewModel(checklistId, dispatchersProviders, taskDao) {

    private val _stateScreen =
        MutableStateFlow<ChecklistDetailState>(ChecklistDetailState.Overview)

    val stateScreen = _stateScreen.asStateFlow()

    fun changeState(state: ChecklistDetailState) {
        _stateScreen.value = state
    }

    fun deleteChecklist() {
        viewModelScope.launch(dispatchersProviders.io) {
            val checklist = checklistDao.getChecklist(checklistId)
            checklistDao.delete(checklist)
            changeState(ChecklistDetailState.Delete)
        }
    }

}

sealed class ChecklistDetailState {
    object Overview : ChecklistDetailState()
    object Edit : ChecklistDetailState()
    object Delete : ChecklistDetailState()
}