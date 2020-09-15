package wottrich.github.io.tools.dispatcher

import kotlinx.coroutines.CoroutineDispatcher

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 12/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
interface DispatchersProviders {

    val main: CoroutineDispatcher

    val io: CoroutineDispatcher

}