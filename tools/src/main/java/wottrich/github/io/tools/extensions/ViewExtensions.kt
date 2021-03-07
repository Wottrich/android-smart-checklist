package wottrich.github.io.tools.extensions

import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/12/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
val ViewGroup.inflater: LayoutInflater
    get() = LayoutInflater.from(this.context)