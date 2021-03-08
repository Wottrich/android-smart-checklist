package wottrich.github.io.tools.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 17/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
fun AppCompatActivity.navHost(@IdRes fragmentContainerViewID: Int)
        = supportFragmentManager.findFragmentById(fragmentContainerViewID) as NavHostFragment

inline fun <reified T: Activity> Activity?.startActivity(
    finishActualActivity: Boolean = false,
    options: Bundle? = null,
    setupIntent: Intent.() -> Unit = {}
) {
    val intent = Intent(this, T::class.java)
    setupIntent(intent)
    this?.startActivity(intent, options)
    if (finishActualActivity) {
        this?.finish()
    }
}