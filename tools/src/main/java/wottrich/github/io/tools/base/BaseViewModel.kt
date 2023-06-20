package wottrich.github.io.tools.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import github.io.wottrich.coroutines.dispatcher.DispatchersProviders
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 28/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

abstract class BaseViewModel(
    private val dispatchersProviders: DispatchersProviders = GlobalContext.get().get()
) : ViewModel() {

    fun io() = viewModelScope.coroutineContext + dispatchersProviders.io

    fun main() = viewModelScope.coroutineContext + dispatchersProviders.main

    fun launchMain(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = launch(main(), start, block)

    fun launchIO(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = launch(io(), start, block)

    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(context, start, block)

}