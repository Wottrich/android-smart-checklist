package wottrich.github.io.androidsmartchecklist.injection

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.SplashViewModel

val splashModule = module {
    viewModel { SplashViewModel(get()) }
}