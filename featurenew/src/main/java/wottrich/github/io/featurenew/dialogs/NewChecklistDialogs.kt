package wottrich.github.io.featurenew.dialogs

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import wottrich.github.io.featurenew.R
import wottrich.github.io.featurenew.view.ChecklistNameFragment
import wottrich.github.io.tools.extensions.defaultErrorDialog

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 15/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

fun Fragment.showDefaultErrorMessageDialog() {
    showErrorDialog(R.string.unknown)
}

fun Fragment?.showErrorDialog(@StringRes message: Int) {
    this?.context?.defaultErrorDialog(message)
}

fun Fragment?.showErrorDialog(message: String) {
    this?.context?.defaultErrorDialog(message)
}