package wottrich.github.io.featurenew.view.screens.tasklist

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import wottrich.github.io.database.entity.Task
import wottrich.github.io.featurenew.domain.usecase.AddTaskUseCase
import wottrich.github.io.featurenew.domain.usecase.DeleteTaskUseCase
import wottrich.github.io.featurenew.domain.usecase.LoadTaskUseCase
import wottrich.github.io.featurenew.domain.usecase.UpdateTaskUseCase
import wottrich.github.io.featurenew.extensions.updateLocalTaskList
import wottrich.github.io.tools.dispatcher.DispatchersProviders

open class TaskListViewModel(
    private val checklistId: String,
    private val dispatchersProviders: DispatchersProviders,
    private val getLoadTaskUseCase: LoadTaskUseCase,
    private val getAddTaskUseCase: AddTaskUseCase,
    private val getDeleteTaskUseCase: DeleteTaskUseCase,
    private val getUpdateTaskUseCase: UpdateTaskUseCase,
) : ViewModel() {

    var tasks = mutableStateListOf<Task>()
        private set

    init {
        viewModelScope.launch(dispatchersProviders.main) {
            val loadedTasks = getLoadTaskUseCase(checklistId)
            tasks.addAll(loadedTasks)
        }
    }

    fun onAddClicked(taskName: String) {
        addTaskAndClearText(taskName)
    }

    fun onDeleteClicked(task: Task) {
        viewModelScope.launch(dispatchersProviders.io) {
            getDeleteTaskUseCase(task)
            tasks.remove(task)
        }
    }

    fun onUpdateClicked(task: Task) {
        viewModelScope.launch(dispatchersProviders.io) {
            tasks.updateLocalTaskList(task)
            getUpdateTaskUseCase(task)
        }
    }

    private fun addTaskAndClearText(taskName: String) {
        viewModelScope.launch(dispatchersProviders.io) {
            val task = getAddTaskUseCase(checklistId.toLong(), taskName)
            tasks.add(task)
        }
    }

}