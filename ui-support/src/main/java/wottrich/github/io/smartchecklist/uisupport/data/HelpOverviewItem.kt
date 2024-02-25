package wottrich.github.io.smartchecklist.uisupport.data

import androidx.annotation.StringRes

data class HelpOverviewItem(
    @StringRes val label: Int,
    @StringRes val description: Int,
    val deeplink: String
)