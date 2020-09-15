package wottrich.github.io.tools.extensions

import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

fun Long?.formatDate () : String? {
    if (this == null) {
        return null
    }

    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateConverted = Date(this)

    return try {
        formatter.format(dateConverted)
    } catch (e: Exception) {
        null
    }

}