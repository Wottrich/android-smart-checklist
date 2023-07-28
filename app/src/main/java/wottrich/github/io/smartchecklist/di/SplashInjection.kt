package wottrich.github.io.smartchecklist.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.presentation.viewmodel.SplashViewModel

val splashModule = module {
    viewModel { SplashViewModel() }
}