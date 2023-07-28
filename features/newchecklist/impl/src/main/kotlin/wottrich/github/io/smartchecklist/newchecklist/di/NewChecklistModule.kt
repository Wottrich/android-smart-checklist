package wottrich.github.io.smartchecklist.newchecklist.di

import wottrich.github.io.smartchecklist.newchecklist.domain.AddNewChecklistUseCase
import wottrich.github.io.smartchecklist.newchecklist.presentation.viewmodels.NewChecklistNameViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val newChecklistModule = module {
    factory { AddNewChecklistUseCase(get()) }
    viewModel {
        NewChecklistNameViewModel(get(), get(), get())
    }
}