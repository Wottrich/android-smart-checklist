package github.io.wottrich.coroutines.di

import github.io.wottrich.coroutines.dispatcher.AppDispatcher
import github.io.wottrich.coroutines.dispatcher.DispatchersProviders
import org.koin.dsl.module

val coroutinesModule = module {
    single<DispatchersProviders> { AppDispatcher }
}