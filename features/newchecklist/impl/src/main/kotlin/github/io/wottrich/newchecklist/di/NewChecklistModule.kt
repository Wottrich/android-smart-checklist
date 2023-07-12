package github.io.wottrich.newchecklist.di

import github.io.wottrich.newchecklist.domain.AddNewChecklistUseCase
import github.io.wottrich.newchecklist.presentation.viewmodels.NewChecklistNameViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val newChecklistModule = module {
    factory { AddNewChecklistUseCase(get()) }
    viewModel {
        NewChecklistNameViewModel(get(), get(), get())
    }
}