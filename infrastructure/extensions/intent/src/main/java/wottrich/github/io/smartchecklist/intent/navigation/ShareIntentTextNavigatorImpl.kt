package wottrich.github.io.smartchecklist.intent.navigation

import android.content.Context
import wottrich.github.io.smartchecklist.intent.shareIntentText

internal class ShareIntentTextNavigatorImpl(private val context: Context) : ShareIntentTextNavigator {
    override fun shareIntentText(text: String) {
        context.shareIntentText(text)
    }
}