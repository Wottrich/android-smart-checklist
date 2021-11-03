package wottrich.github.io.featurenew.view.screens.checklistname

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import wottrich.github.io.database.dao.ChecklistDao
import wottrich.github.io.database.entity.Checklist
import wottrich.github.io.tools.SingleShotEventBus
import wottrich.github.io.tools.dispatcher.DispatchersProviders

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

class ChecklistNameViewModel(
    private val dispatchersProviders: DispatchersProviders,
    private val database: ChecklistDao
) : ViewModel() {
    private val _nextScreenEvent = SingleShotEventBus<String>()
    val nextScreenEvent = _nextScreenEvent.events

    private val _state = SingleShotEventBus<ChecklistNameScreenState>()
    val state = _state.events

    fun nextScreen(checklistName: String) {
        viewModelScope.launch(dispatchersProviders.io) {
            val itemId = database.insert(Checklist(name = checklistName))
            if (itemId != null) {
                _state.postEvent(ChecklistNameScreenState.NextScreen(itemId.toString()))
                //_nextScreenEvent.postEvent(itemId.toString())
            } else {
                _state.postEvent(ChecklistNameScreenState.InvalidChecklistState)
            }
        }
    }

    fun updateState(state: ChecklistNameScreenState) {
        viewModelScope.launch(dispatchersProviders.io) {
            _state.postEvent(state)
        }
    }

}

sealed class ChecklistNameScreenState(val hasError: Boolean = false) {
    object InitialState : ChecklistNameScreenState()
    data class NextScreen(val checklistId: String) : ChecklistNameScreenState()
    object InvalidChecklistState : ChecklistNameScreenState(hasError = true)
}