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
 
class TaskListViewModel(
    private val checklistId: Long?
) : ViewModel() {

    val taskName = MutableLiveData<String>()

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage = _errorMessage as LiveData<String>

    private val _tasks = MutableLiveData<List<Task>?>(listOf())
    val tasks: LiveData<List<Task>?> = _tasks

    fun verifyTaskNameToAddItem() {
        val taskName = taskName.value
        if (!taskName.isNullOrEmpty()) {
            addTaskAndClearText(taskName)
        } else {
            _errorMessage.value = ERROR_EMPTY_TASK
        }
    }

    private fun addTaskAndClearText(taskName: String) {
        val newTaskList = _tasks.value?.toMutableList()?.apply {
            add(generateTask(taskName))
        }.orEmpty()
        _tasks.value = newTaskList
        clearTaskName()
    }

    fun deleteTask(task: Task) {
        val newTaskList = _tasks.value?.toMutableList()?.apply {
            remove(task)
        }.orEmpty()
        _tasks.value = newTaskList
    }

    fun updateTask(task: Task) {
        val newTaskList = _tasks.value?.toMutableList()?.apply {
            this.find { it == task }?.isCompleted = !task.isCompleted
        }.orEmpty()
        _tasks.value = newTaskList
    }

    private fun generateTask(taskName: String): Task {
        return Task(name = taskName)
    }

    private fun clearTaskName() {
        taskName.value = ""
    }

    companion object {
        const val ERROR_EMPTY_TASK = "TaskListViewModelEmptyTask"
        const val ERROR_DELETE_TASK = "TaskListViewModelDeleteTask"
    }

}