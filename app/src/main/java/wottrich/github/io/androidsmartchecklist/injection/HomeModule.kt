package wottrich.github.io.androidsmartchecklist.injection

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import wottrich.github.io.androidsmartchecklist.view.HomeViewModel

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 12/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
val featureHomeModules = module {

    viewModel { HomeViewModel(get(), get()) }

}