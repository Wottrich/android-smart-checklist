package wottrich.github.io.tools.extensions

import androidx.lifecycle.Lifecycle

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 04/11/2021
 *
 * Copyright © 2021 android-smart-checklist. All rights reserved.
 *
 */
 
fun Lifecycle.isResumedState() = this.currentState == Lifecycle.State.RESUMED