package wottrich.github.io.tools.utils

import android.content.ClipData
import android.content.ClipboardManager

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 27/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

interface Clipboard {
    fun copy(clipData: ClipData)
}

class ClipboardImpl(
    private val manager: ClipboardManager
) : Clipboard {
    override fun copy(clipData: ClipData) {
        manager.setPrimaryClip(clipData)
    }
}