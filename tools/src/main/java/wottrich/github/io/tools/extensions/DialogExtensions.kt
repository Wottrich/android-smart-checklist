package wottrich.github.io.tools.extensions

import android.content.Context
import androidx.annotation.StringRes
import wottrich.github.io.tools.R

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 17/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
fun Context?.defaultErrorDialog (@StringRes messageRes: Int) {
    defaultErrorDialog(this?.getString(messageRes))
}

fun Context?.defaultErrorDialog (message: String?) {
    showAlertDialog {
        setTitle(context.getString(R.string.attention))
        setMessage(message)
        neutralButton {
            title = "OK"
        }
    }
}