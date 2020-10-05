package wottrich.github.io.featurenew.view.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wottrich.github.io.database.entity.Task

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 21/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
class TaskListViewModel : ViewModel() {

    val taskName = MutableLiveData<String>()

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage = _errorMessage as LiveData<String>

    private val listOfTaskName = mutableListOf<Task>()

    fun verifyTaskNameToAddItem() {
        val taskName = taskName.value
        if (!taskName.isNullOrEmpty()) {
            addTaskAndClearText(taskName)
        } else {
            _errorMessage.value = ERROR_EMPTY_TASK
        }
    }

    private fun addTaskAndClearText(taskName: String) {
        listOfTaskName.add(generateTask(taskName))
        clearTaskName()
    }

    private fun generateTask(taskName: String): Task {
        return Task(name = taskName)
    }

    private fun clearTaskName() {
        taskName.value = ""
    }

    companion object {
        const val ERROR_EMPTY_TASK = "TaskListViewModelEmptyTask"
    }

}