package wottrich.github.io.androidsmartchecklist.di

import android.content.ClipboardManager
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appDefaultModule = module {
    single<ClipboardManager> {
        androidContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
}