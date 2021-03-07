package wottrich.github.io.featurenew.view

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import wottrich.github.io.database.dao.ChecklistDao
import wottrich.github.io.database.entity.Checklist
import wottrich.github.io.featurenew.R
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

    val checklistName = MutableLiveData<String>()

    private val _action = MutableLiveData<ChecklistNameAction>()
    val action: LiveData<ChecklistNameAction> = _action

    private fun canContinue () : Boolean = checklistName.value?.isEmpty() == false

    fun nextScreen() {
        if (canContinue()) {
            viewModelScope.launch(dispatchersProviders.io) {
                val itemId = database.insert(Checklist(name = checklistName.value.orEmpty()))
                _action.postValue(ChecklistNameAction.NextScreen(itemId))
            }
        } else {
            _action.postValue(ChecklistNameAction.ErrorMessage(R.string.fragment_new_checklist_error_continue))
        }
    }

}

sealed class ChecklistNameAction {
    data class NextScreen(val checklistId: Long?): ChecklistNameAction()
    data class ErrorMessage(@StringRes val stringRes: Int): ChecklistNameAction()
}