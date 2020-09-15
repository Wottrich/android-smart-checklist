package wottrich.github.io.tools.injection

import org.koin.dsl.module
import wottrich.github.io.tools.dispatcher.AppDispatcher
import wottrich.github.io.tools.dispatcher.DispatchersProviders

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 12/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

val toolsDispatcherModule = module {

    single<DispatchersProviders> { AppDispatcher }

}