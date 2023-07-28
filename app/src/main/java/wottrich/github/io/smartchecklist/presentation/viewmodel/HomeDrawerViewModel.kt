package wottrich.github.io.smartchecklist.presentation.viewmodel

import github.io.wottrich.checklist.domain.DeleteChecklistUseCase
import github.io.wottrich.checklist.domain.UpdateSelectedChecklistUseCase
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import wottrich.github.io.smartchecklist.domain.usecase.GetChecklistDrawerUseCase
import wottrich.github.io.smartchecklist.presentation.ui.model.HomeDrawerChecklistItemModel
import wottrich.github.io.smartchecklist.presentation.viewmodel.HomeDrawerEvent.DeleteChecklistClicked
import wottrich.github.io.smartchecklist.presentation.viewmodel.HomeDrawerEvent.EditModeClicked
import wottrich.github.io.smartchecklist.presentation.viewmodel.HomeDrawerEvent.ItemClicked
import github.io.wottrich.kotlin.SingleShotEventBus
import github.io.wottrich.android.BaseViewModel

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
    private val getChecklistDrawerUseCase: GetChecklistDrawerUseCase,
    private val updateSelectedChecklistUseCase: UpdateSelectedChecklistUseCase,
    private val deleteChecklistUseCase: DeleteChecklistUseCase
) : BaseViewModel(dispatchers) {

    private val _drawerStateFlow: MutableStateFlow<HomeDrawerState> =
        MutableStateFlow(HomeDrawerState.Loading)
    val drawerStateFlow: StateFlow<HomeDrawerState> = _drawerStateFlow

    private val _drawerEffectFlow = SingleShotEventBus<HomeDrawerEffect?>()
    val drawerEffectFlow: Flow<HomeDrawerEffect?> = _drawerEffectFlow.events

    init {
        launchIO {
            getChecklistDrawerUseCase().collect(
                FlowCollector { result ->
                    result.onSuccess { checklists ->
                        _drawerStateFlow.value = HomeDrawerState.Content(checklists)
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

    private fun onItemClicked(checklistItemModel: HomeDrawerChecklistItemModel) {
        launchIO {
            updateSelectedChecklistUseCase(checklistItemModel.checklistUuid)
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

    private fun handleDeleteChecklist(checklistItemModel: HomeDrawerChecklistItemModel) {
        launchIO {
            deleteChecklistUseCase(checklistItemModel.checklistUuid)
        }
    }
}

sealed class HomeDrawerState {
    object Loading : HomeDrawerState()
    data class Content(
        val checklists: List<HomeDrawerChecklistItemModel>,
        val isEditing: Boolean = false
    ) : HomeDrawerState()
}

sealed class HomeDrawerEvent {
    data class ItemClicked(val checklistWithTasks: HomeDrawerChecklistItemModel) : HomeDrawerEvent()
    object EditModeClicked : HomeDrawerEvent()
    data class DeleteChecklistClicked(
        val checklistWithTasks: HomeDrawerChecklistItemModel
    ) : HomeDrawerEvent()
}

sealed class HomeDrawerEffect {
    object CloseDrawer : HomeDrawerEffect()

    override fun equals(other: Any?): Boolean = false
    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}