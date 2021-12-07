package wottrich.github.io.androidsmartchecklist.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import wottrich.github.io.database.dao.ChecklistDao
import wottrich.github.io.database.entity.Checklist
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
    dispatchers: DispatchersProviders,
    private val dao: ChecklistDao
) : ViewModel() {

    private val _drawerStateFlow = MutableStateFlow(HomeDrawerState())
    val drawerStateFlow: StateFlow<HomeDrawerState> = _drawerStateFlow

    init {
        viewModelScope.launch(dispatchers.io) {
            dao.observeChecklistsUpdate().collect {
                _drawerStateFlow.value =
                    drawerStateFlow.value.copy(isLoading = false, checklists = it)
            }
        }
    }

}

data class HomeDrawerState(
    val isLoading: Boolean = true,
    val checklists: List<Checklist> = emptyList()
)