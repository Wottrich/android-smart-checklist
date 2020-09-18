package wottrich.github.io.featurenew.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wottrich.github.io.database.dao.ChecklistDao

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

class NewChecklistViewModel : ViewModel() {

    val checklistName = MutableLiveData<String>()

    private val _navigation = MutableLiveData<String>()
    val navigation = _navigation as LiveData<String>

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage = _errorMessage as LiveData<String>

    private fun canContinue () : Boolean = !checklistName.value.isNullOrEmpty()

    fun nextScreen() {
        if (canContinue()) {
            _navigation.postValue(checklistName.value)
        } else {
            _errorMessage.postValue(ERROR_CONTINUE)
        }
    }

    companion object {
        const val ERROR_CONTINUE = "NewChecklistViewModelErrorContinue"
    }

}