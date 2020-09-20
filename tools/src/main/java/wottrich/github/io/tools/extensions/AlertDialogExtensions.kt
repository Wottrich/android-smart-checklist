package wottrich.github.io.tools.extensions

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 15/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

data class ButtonAlertDialog(var title: String? = null, var onClick: (DialogInterface) -> Unit = {})

fun Context?.alertDialogBase(setup: AlertDialog.Builder.() -> Unit) : AlertDialog.Builder {
    val alertDialog = AlertDialog.Builder(this)
    setup(alertDialog)
    return alertDialog
}

fun AlertDialog.Builder.positiveButton (setup: ButtonAlertDialog.() -> Unit) {
    val btnAlert = ButtonAlertDialog()
    setup(btnAlert)
    setPositiveButton(btnAlert.title) { dialog, _ ->
        btnAlert.onClick(dialog)
    }
}

fun AlertDialog.Builder.negativeButton (setup: ButtonAlertDialog.() -> Unit) {
    val btnAlert = ButtonAlertDialog()
    setup(btnAlert)
    setNegativeButton(btnAlert.title) { dialog, _ ->
        btnAlert.onClick(dialog)
    }
}

fun AlertDialog.Builder.neutralButton (setup: ButtonAlertDialog.() -> Unit) {
    val btnAlert = ButtonAlertDialog()
    setup(btnAlert)
    setNeutralButton(btnAlert.title) { dialog, _ ->
        btnAlert.onClick(dialog)
    }
}

fun Context?.showAlertDialog(setup: AlertDialog.Builder.() -> Unit) {
    alertDialogBase {
        setup(this)
    }.show()
}