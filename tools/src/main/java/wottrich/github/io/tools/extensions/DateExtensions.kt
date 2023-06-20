package wottrich.github.io.tools.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

fun Long?.validAndFormatDate(
    pattern: String = "dd/MM/yyyy",
    locale: Locale = Locale.getDefault()
): String? {
    return this?.formatDate(pattern, locale)
}

private fun Long.formatDate(
    pattern: String = "dd/MM/yyyy",
    locale: Locale = Locale.getDefault()
): String? {
    val formatter = SimpleDateFormat(pattern, locale)
    val dateConverted = Date(this)

    return try {
        formatter.format(dateConverted)
    } catch (e: Exception) {
        null
    }
}