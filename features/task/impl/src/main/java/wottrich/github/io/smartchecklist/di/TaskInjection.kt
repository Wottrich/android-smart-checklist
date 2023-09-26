package wottrich.github.io.smartchecklist.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.domain.usecase.AddManyTasksUseCase
import wottrich.github.io.smartchecklist.domain.usecase.ChangeTasksCompletedStatusUseCase
import wottrich.github.io.smartchecklist.domain.usecase.AddTaskToDatabaseUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetChangeTaskStatusUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetDeleteTaskUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetTasksFromSelectedChecklistUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetTasksUseCase
import wottrich.github.io.smartchecklist.domain.usecase.ReverseTasksIfNeededUseCase
import wottrich.github.io.smartchecklist.domain.usecase.SortTasksBySelectedSortUseCase
import wottrich.github.io.smartchecklist.presentation.viewmodel.TaskComponentViewModel

val taskModule = module {
    injectUseCases()
    injectViewModels()
}

private fun Module.injectUseCases() {
    factory { ChangeTasksCompletedStatusUseCase(get()) }
    factory { AddTaskToDatabaseUseCase(get()) }
    factory { GetChangeTaskStatusUseCase(get()) }
    factory { GetDeleteTaskUseCase(get()) }
    factory { GetTasksUseCase(get()) }
    factory { AddManyTasksUseCase(get()) }
    factory { GetTasksFromSelectedChecklistUseCase(get()) }
    factory { SortTasksBySelectedSortUseCase() }
    factory { ReverseTasksIfNeededUseCase() }
}

private fun Module.injectViewModels() {
    viewModel {
        TaskComponentViewModel(
            getTasksFromSelectedChecklistUseCase = get(),
            addTaskToDatabaseUseCase = get(),
            getChangeTaskStatusUseCase = get(),
            getDeleteTaskUseCase = get(),
            sortTasksBySelectedSortUseCase = get(),
            reverseTasksIfNeededUseCase = get()
        )
    }
}