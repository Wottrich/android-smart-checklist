package wottrich.github.io.coroutines.di

import wottrich.github.io.coroutines.dispatcher.AppDispatcher
import wottrich.github.io.coroutines.dispatcher.DispatchersProviders
import org.koin.dsl.module

val coroutinesModule = module {
    single<DispatchersProviders> { AppDispatcher }
}