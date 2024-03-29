package wottrich.github.io.smartchecklist.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    protected fun io() = viewModelScope.coroutineContext + dispatchersProviders.io

    protected fun main() = viewModelScope.coroutineContext + dispatchersProviders.main

    protected suspend fun withMainContext(
        block: suspend CoroutineScope.() -> Unit
    ) = withContext(main(), block)

    protected fun launchMain(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = launch(main(), start, block)

    protected fun launchIO(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = launch(io(), start, block)

    protected fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(context, start, block)

}