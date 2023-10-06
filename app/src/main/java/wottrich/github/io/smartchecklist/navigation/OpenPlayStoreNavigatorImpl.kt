package wottrich.github.io.smartchecklist.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

class OpenPlayStoreNavigatorImpl(private val context: Context) : OpenPlayStoreNavigator {
    override fun openPlayStore() {
        val packageName = context.packageName
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}