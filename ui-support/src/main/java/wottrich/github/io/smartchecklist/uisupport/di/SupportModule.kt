package wottrich.github.io.smartchecklist.uisupport.di

import wottrich.github.io.smartchecklist.uisupport.domain.GetHelpOverviewItemsUseCase
import wottrich.github.io.smartchecklist.uisupport.presentation.viewmodels.HelpOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.uisupport.navigation.SupportContextNavigator

val supportModule = module {
    single { SupportContextNavigator() } bind SmartChecklistNavigation::class
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