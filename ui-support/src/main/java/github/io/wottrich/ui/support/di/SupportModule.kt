package github.io.wottrich.ui.support.di

import github.io.wottrich.ui.support.domain.GetHelpOverviewItemsUseCase
import github.io.wottrich.ui.support.presentation.viewmodels.HelpOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val supportModule = module {
    factory {
        GetHelpOverviewItemsUseCase()
    }
    viewModel {
        HelpOverviewViewModel(
            get(),
            get()
        )
    }
}