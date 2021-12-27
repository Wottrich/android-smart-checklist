package wottrich.github.io.tools.injection

import android.content.ClipboardManager
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import wottrich.github.io.tools.dispatcher.AppDispatcher
import wottrich.github.io.tools.dispatcher.DispatchersProviders
import wottrich.github.io.tools.utils.Clipboard
import wottrich.github.io.tools.utils.ClipboardImpl

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 12/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

val toolsDispatcherModule = module {

    single<ClipboardManager> {
        androidContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
    single<DispatchersProviders> { AppDispatcher }

    factory<Clipboard> { ClipboardImpl(get()) }

}