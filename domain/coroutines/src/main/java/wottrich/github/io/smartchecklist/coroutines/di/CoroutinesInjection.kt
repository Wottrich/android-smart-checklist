package wottrich.github.io.smartchecklist.coroutines.di

import wottrich.github.io.smartchecklist.coroutines.dispatcher.AppDispatcher
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import org.koin.dsl.module

val coroutinesModule = module {
    single<DispatchersProviders> { AppDispatcher }
}