package wottrich.github.io.smartchecklist.di

import org.koin.core.module.Module
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.domain.usecase.AddManyTasksUseCase
import wottrich.github.io.smartchecklist.domain.usecase.ChangeTasksCompletedStatusUseCase
import wottrich.github.io.smartchecklist.domain.usecase.AddTaskToDatabaseUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetChangeTaskStatusUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetDeleteTaskUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetTasksFromSelectedChecklistUseCase
import wottrich.github.io.smartchecklist.domain.usecase.GetTasksUseCase

val taskModule = module {
    injectUseCases()
}

private fun Module.injectUseCases() {
    factory { ChangeTasksCompletedStatusUseCase(get()) }
    factory { AddTaskToDatabaseUseCase(get()) }
    factory { GetChangeTaskStatusUseCase(get()) }
    factory { GetDeleteTaskUseCase(get()) }
    factory { GetTasksUseCase(get()) }
    factory { AddManyTasksUseCase(get()) }
    factory { GetTasksFromSelectedChecklistUseCase(get()) }
}