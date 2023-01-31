package wottrich.github.io.impl.di

import org.koin.core.module.Module
import org.koin.dsl.module
import wottrich.github.io.impl.domain.usecase.AddManyTasksUseCase
import wottrich.github.io.impl.domain.usecase.ChangeTasksCompletedStatusUseCase
import wottrich.github.io.impl.domain.usecase.GetAddTaskUseCase
import wottrich.github.io.impl.domain.usecase.GetChangeTaskStatusUseCase
import wottrich.github.io.impl.domain.usecase.GetDeleteTaskUseCase
import wottrich.github.io.impl.domain.usecase.GetTasksUseCase

val taskModule = module {
    injectUseCases()
}

private fun Module.injectUseCases() {
    factory { ChangeTasksCompletedStatusUseCase(get()) }
    factory { GetAddTaskUseCase(get()) }
    factory { GetChangeTaskStatusUseCase(get()) }
    factory { GetDeleteTaskUseCase(get()) }
    factory { GetTasksUseCase(get()) }
    factory { AddManyTasksUseCase(get()) }
}