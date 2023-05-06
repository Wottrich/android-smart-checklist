package github.io.wottrich.checklist.di

import github.io.wottrich.checklist.domain.usecase.DeleteChecklistUseCase
import github.io.wottrich.checklist.domain.usecase.GetChecklistWithTaskUseCase
import github.io.wottrich.checklist.domain.usecase.GetSelectedChecklistUseCase
import github.io.wottrich.checklist.domain.usecase.UpdateSelectedChecklistUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val checklistModule = module {
    injectUseCases()
}

private fun Module.injectUseCases() {
    factory { GetChecklistWithTaskUseCase(get()) }
    factory { DeleteChecklistUseCase(get()) }
    factory { GetSelectedChecklistUseCase(get()) }
    factory { UpdateSelectedChecklistUseCase(get()) }
}