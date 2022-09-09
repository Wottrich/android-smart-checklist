package wottrich.github.io.androidsmartchecklist.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HasOldChecklistToMigrateUseCase
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.MigrationUseCase
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.MigrationViewModel
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.SplashViewModel

val splashModule = module {
    factory { MigrationUseCase(get(), get()) }
    factory { HasOldChecklistToMigrateUseCase(get()) }
    viewModel { MigrationViewModel(get()) }
    viewModel { SplashViewModel(get()) }
}