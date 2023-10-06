package wottrich.github.io.smartchecklist.newchecklist.di

import wottrich.github.io.smartchecklist.newchecklist.domain.AddNewChecklistUseCase
import wottrich.github.io.smartchecklist.newchecklist.presentation.viewmodels.NewChecklistNameViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.newchecklist.navigation.NewChecklistContextNavigator

val newChecklistModule = module {
    single { NewChecklistContextNavigator() } bind SmartChecklistNavigation::class
    factory { AddNewChecklistUseCase(get()) }
    viewModel {
        NewChecklistNameViewModel(get(), get(), get())
    }
}