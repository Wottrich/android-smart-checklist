package wottrich.github.io.featurenew.view

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
 
class NewChecklistViewModel(
    private val dao: ChecklistDao
) : ViewModel() {

    val checklistName = MutableLiveData<String>()

    fun canContinue () : Boolean = !checklistName.value.isNullOrEmpty()

}