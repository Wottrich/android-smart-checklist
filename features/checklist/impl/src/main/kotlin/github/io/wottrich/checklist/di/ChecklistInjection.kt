package github.io.wottrich.checklist.di

import github.io.wottrich.checklist.domain.usecase.GetChecklistWithTaskUseCase
import github.io.wottrich.checklist.domain.usecase.GetDeleteChecklistUseCase
import github.io.wottrich.checklist.domain.usecase.GetSelectedChecklistUseCase
import github.io.wottrich.checklist.domain.usecase.GetUpdateSelectedChecklistUseCase
import github.io.wottrich.checklist.presentation.viewmodel.ChecklistNameViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val checklistModule = module {
    injectUseCases()
    injectViewModels()
}

private fun Module.injectViewModels() {
    viewModel { ChecklistNameViewModel(get(), get()) }
}

private fun Module.injectUseCases() {
    factory { GetChecklistWithTaskUseCase(get()) }
    factory { GetDeleteChecklistUseCase(get()) }
    factory { GetSelectedChecklistUseCase(get()) }
    factory { GetUpdateSelectedChecklistUseCase(get()) }
}