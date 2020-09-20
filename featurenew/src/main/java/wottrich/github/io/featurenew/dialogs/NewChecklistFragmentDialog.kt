package wottrich.github.io.featurenew.dialogs

import androidx.annotation.StringRes
import wottrich.github.io.featurenew.R
import wottrich.github.io.featurenew.view.NewChecklistFragment
import wottrich.github.io.tools.extensions.defaultErrorDialog
import wottrich.github.io.tools.extensions.neutralButton
import wottrich.github.io.tools.extensions.showAlertDialog

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 15/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

fun NewChecklistFragment?.showErrorDialog(@StringRes message: Int) {
    this?.context?.defaultErrorDialog(message)
}

fun NewChecklistFragment?.showErrorDialog(message: String) {
    this?.context?.defaultErrorDialog(message)
}