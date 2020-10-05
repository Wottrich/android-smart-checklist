package wottrich.github.io.tools.extensions

import android.content.Context
import android.view.LayoutInflater

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 04/10/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)