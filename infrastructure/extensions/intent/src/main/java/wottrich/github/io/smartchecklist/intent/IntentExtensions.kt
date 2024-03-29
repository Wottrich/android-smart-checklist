package wottrich.github.io.smartchecklist.intent

import android.content.Context
import android.content.Intent

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 27/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */
 
fun Context.shareIntentText(text: String) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(intent, null).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(shareIntent)
}