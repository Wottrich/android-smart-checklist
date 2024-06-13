package wottrich.github.io.smartchecklist.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import wottrich.github.io.smartchecklist.BuildConfig

class PrivacyPolicyImpl(private val context: Context) : PrivacyPolicy {
    private val url = BuildConfig.PRIVACY_POLICY_URL
    override fun openPrivacyPolicy() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).also {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(browserIntent)
    }
}