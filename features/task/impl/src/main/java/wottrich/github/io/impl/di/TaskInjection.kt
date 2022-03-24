package wottrich.github.io.impl.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import wottrich.github.io.publicandroid.domain.usecase.ChangeTasksCompletedStatusUseCase
import wottrich.github.io.publicandroid.domain.usecase.GetAddTaskUseCase
import wottrich.github.io.publicandroid.domain.usecase.GetChangeTaskStatusUseCase
import wottrich.github.io.publicandroid.domain.usecase.GetDeleteTaskUseCase
import wottrich.github.io.publicandroid.domain.usecase.GetTasksUseCase
import wottrich.github.io.publicandroid.domain.usecase.ObserveTasksUseCase
import wottrich.github.io.publicandroid.presentation.viewmodel.TaskListViewModel

val taskModule = module {
    injectUseCases()
    injectViewModels()
}

private fun Module.injectViewModels() {
    viewModel { (checklistId: String) ->
        TaskListViewModel(checklistId, get(), get(), get(), get(), get())
    }
}

private fun Module.injectUseCases() {
    factory { ChangeTasksCompletedStatusUseCase(get()) }
    factory { GetAddTaskUseCase(get()) }
    factory { GetChangeTaskStatusUseCase(get()) }
    factory { GetDeleteTaskUseCase(get()) }
    factory { GetTasksUseCase(get()) }
    factory { ObserveTasksUseCase(get()) }
}