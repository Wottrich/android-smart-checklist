package wottrich.github.io.androidsmartchecklist.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import github.io.wottrich.checklist.domain.usecase.DeleteChecklistUseCase
import github.io.wottrich.checklist.domain.usecase.GetChecklistWithTaskUseCase
import github.io.wottrich.checklist.domain.usecase.UpdateSelectedChecklistUseCase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerEvent.DeleteChecklistClicked
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerEvent.EditModeClicked
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerEvent.ItemClicked
import wottrich.github.io.datasource.entity.NewChecklistWithNewTasks
import wottrich.github.io.tools.SingleShotEventBus
import wottrich.github.io.tools.base.BaseViewModel
import github.io.wottrich.coroutines.base.onSuccess
import github.io.wottrich.coroutines.dispatcher.DispatchersProviders

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 12/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

@OptIn(InternalCoroutinesApi::class)
class HomeDrawerViewModel(
    dispatchers: DispatchersProviders,
    private val getChecklistWithTaskUseCase: GetChecklistWithTaskUseCase,
    private val updateSelectedChecklistUseCase: UpdateSelectedChecklistUseCase,
    private val deleteChecklistUseCase: DeleteChecklistUseCase
) : BaseViewModel(dispatchers) {

    private val _drawerStateFlow: MutableStateFlow<HomeDrawerState> =
        MutableStateFlow(HomeDrawerState.Loading)
    val drawerStateFlow: StateFlow<HomeDrawerState> = _drawerStateFlow

    private val _drawerEffectFlow = SingleShotEventBus<HomeDrawerEffect?>()
    val drawerEffectFlow: Flow<HomeDrawerEffect?> = _drawerEffectFlow.events

    init {
        viewModelScope.launch(dispatchers.io) {
            getChecklistWithTaskUseCase().collect(
                FlowCollector {
                    it.onSuccess {
                        _drawerStateFlow.value = HomeDrawerState.Content(it)
                    }
                }
            )
        }
    }

    fun processEvent(event: HomeDrawerEvent) {
        when (event) {
            is ItemClicked -> onItemClicked(event.checklistWithTasks)
            is DeleteChecklistClicked -> handleDeleteChecklist(event.checklistWithTasks)
            EditModeClicked -> handleEditMode()
        }
    }

    fun clearEffect() {
        launchIO {
            _drawerEffectFlow.emit(null)
        }
    }

    private fun onItemClicked(checklistWithTasks: NewChecklistWithNewTasks) {
        launchIO {
            updateSelectedChecklistUseCase(checklistWithTasks.newChecklist)
            _drawerEffectFlow.emit(HomeDrawerEffect.CloseDrawer)
            // Observer applied at init will update content with new value
            // on the other hand if the observer doesn't exist
            // we should call _drawerStateFlow.value = HomeDrawerState.Content(listUpdated)
            // or call repository to get new values
        }
    }

    private fun handleEditMode() {
        val state = drawerStateFlow.value as HomeDrawerState.Content
        _drawerStateFlow.value = state.copy(isEditing = !state.isEditing)
    }

    private fun handleDeleteChecklist(checklistWithTasks: NewChecklistWithNewTasks) {
        launchIO {
            deleteChecklistUseCase(checklistWithTasks.newChecklist)
        }
    }
}

sealed class HomeDrawerState {
    object Loading : HomeDrawerState()
    data class Content(
        val checklists: List<NewChecklistWithNewTasks>,
        val isEditing: Boolean = false
    ) : HomeDrawerState()
}

sealed class HomeDrawerEvent {
    data class ItemClicked(val checklistWithTasks: NewChecklistWithNewTasks) : HomeDrawerEvent()
    object EditModeClicked : HomeDrawerEvent()
    data class DeleteChecklistClicked(
        val checklistWithTasks: NewChecklistWithNewTasks
    ) : HomeDrawerEvent()
}

sealed class HomeDrawerEffect {
    object CloseDrawer : HomeDrawerEffect()

    override fun equals(other: Any?): Boolean = false
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}