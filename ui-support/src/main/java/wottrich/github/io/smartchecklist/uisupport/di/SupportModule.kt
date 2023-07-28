package wottrich.github.io.smartchecklist.uisupport.di

import wottrich.github.io.smartchecklist.uisupport.domain.GetHelpOverviewItemsUseCase
import wottrich.github.io.smartchecklist.uisupport.presentation.viewmodels.HelpOverviewViewModel
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