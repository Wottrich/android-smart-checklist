package wottrich.github.io.tools.extensions

import android.app.Activity
import android.content.Intent
import android.os.Bundle

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 17/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

inline fun <reified T: Activity> Activity?.startActivity(
    finishActualActivity: Boolean = false,
    options: Bundle? = null,
    setupIntent: Intent.() -> Unit = {}
) {
    val intent = Intent(this, T::class.java)
    setupIntent(intent)
    this?.startActivity(intent, options)
    if (finishActualActivity) {
        this?.finish()
    }
}