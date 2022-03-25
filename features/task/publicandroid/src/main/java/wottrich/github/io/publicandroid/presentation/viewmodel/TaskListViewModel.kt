package wottrich.github.io.publicandroid.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wottrich.github.io.database.entity.Task
import wottrich.github.io.publicandroid.domain.usecase.GetAddTaskUseCase
import wottrich.github.io.publicandroid.domain.usecase.GetChangeTaskStatusUseCase
import wottrich.github.io.publicandroid.domain.usecase.GetDeleteTaskUseCase
import wottrich.github.io.publicandroid.domain.usecase.ObserveTasksUseCase
import wottrich.github.io.tools.dispatcher.DispatchersProviders

class TaskListViewModel(
    private val checklistId: String,
    private val dispatchersProviders: DispatchersProviders,
    private val observeTasksUseCase: ObserveTasksUseCase,
    private val getAddTaskUseCase: GetAddTaskUseCase,
    private val getDeleteTaskUseCase: GetDeleteTaskUseCase,
    private val getChangeTaskStatusUseCase: GetChangeTaskStatusUseCase,
) : ViewModel() {

    var tasks = mutableStateListOf<Task>()
        private set

    init {
        viewModelScope.launch(dispatchersProviders.io) {
            observeTasksUseCase(checklistId).collect {
                withContext(dispatchersProviders.main) {
                    tasks.clear()
                    tasks.addAll(it)
                }
            }
        }
    }

    fun onAddClicked(taskName: String) {
        addTaskAndClearText(taskName)
    }

    fun onDeleteClicked(task: Task) {
        viewModelScope.launch(dispatchersProviders.io) {
            getDeleteTaskUseCase(task)
        }
    }

    fun onUpdateClicked(task: Task) {
        viewModelScope.launch(dispatchersProviders.io) {
            getChangeTaskStatusUseCase(task)
        }
    }

    private fun addTaskAndClearText(taskName: String) {
        viewModelScope.launch(dispatchersProviders.io) {
            getAddTaskUseCase(checklistId.toLong(), taskName)
        }
    }

}