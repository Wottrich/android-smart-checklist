package wottrich.github.io.featurenew.injection

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import wottrich.github.io.featurenew.view.ChecklistNameViewModel
import wottrich.github.io.featurenew.view.task.TaskListViewModel

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
val featureNewModule = module {

    viewModel { ChecklistNameViewModel(get(), get()) }
    viewModel { (checklistId: Long?) -> TaskListViewModel(checklistId) }

}