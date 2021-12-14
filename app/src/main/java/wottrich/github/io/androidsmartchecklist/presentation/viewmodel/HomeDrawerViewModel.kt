package wottrich.github.io.androidsmartchecklist.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.io.wottrich.checklist.domain.usecase.GetChecklistWithTaskUseCase
import github.io.wottrich.checklist.domain.usecase.GetUpdateSelectedChecklistUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import wottrich.github.io.database.entity.ChecklistWithTasks
import wottrich.github.io.tools.SingleShotEventBus
import wottrich.github.io.tools.dispatcher.DispatchersProviders

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 12/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

class HomeDrawerViewModel(
    private val dispatchers: DispatchersProviders,
    private val getChecklistWithTaskUseCase: GetChecklistWithTaskUseCase,
    private val getUpdateSelectedChecklistUseCase: GetUpdateSelectedChecklistUseCase
) : ViewModel() {

    private val _drawerStateFlow: MutableStateFlow<HomeDrawerState> = MutableStateFlow(HomeDrawerState.Loading)
    val drawerStateFlow: StateFlow<HomeDrawerState> = _drawerStateFlow

    private val _drawerEffectFlow = SingleShotEventBus<HomeDrawerEffect?>()
    val drawerEffectFlow: Flow<HomeDrawerEffect?> = _drawerEffectFlow.events

    init {
        viewModelScope.launch(dispatchers.io) {
            getChecklistWithTaskUseCase().collect {
                _drawerStateFlow.value = HomeDrawerState.Content(it)
            }
        }
    }

    fun processEvent(event: HomeDrawerEvent) {
        when (event) {
            is HomeDrawerEvent.ItemClicked -> onItemClicked(event.checklistWithTasks)
        }
    }

    private fun onItemClicked(checklistWithTasks: ChecklistWithTasks) {
        viewModelScope.launch(dispatchers.io) {
            getUpdateSelectedChecklistUseCase(checklistWithTasks.checklist)
            _drawerEffectFlow.postEvent(HomeDrawerEffect.CloseDrawer)
            // Observer applied at init will update content with new value
            // on the other hand if the observer doesn't exist
            // we should call _drawerStateFlow.value = HomeDrawerState.Content(listUpdated)
            // or call repository to get new values
        }
    }
}

sealed class HomeDrawerState {
    object Loading : HomeDrawerState()
    data class Content(val checklists: List<ChecklistWithTasks>) : HomeDrawerState()
}

sealed class HomeDrawerEvent {
    data class ItemClicked(val checklistWithTasks: ChecklistWithTasks) : HomeDrawerEvent()
}

sealed class HomeDrawerEffect {
    object CloseDrawer : HomeDrawerEffect()

    override fun equals(other: Any?): Boolean = false
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}