package wottrich.github.io.androidsmartchecklist.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.ChecklistSettingsViewModel
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerViewModel
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeViewModel

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 12/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
val featureHomeModules = module {
    viewModel { (checklistId: String) ->
        ChecklistSettingsViewModel(
            dispatchersProviders = get(),
            checklistId = checklistId,
            getTasksUseCase = get(),
            changeTasksCompletedStatusUseCase = get()
        )
    }
    viewModel { HomeDrawerViewModel(get(), get(), get(), get()) }
    viewModel {
        HomeViewModel(
            dispatchers = get(),
            getSelectedChecklistUseCase = get(),
            deleteChecklistUseCase = get(),
            getAddTaskUseCase = get(),
            getChangeTaskStatusUseCase = get(),
            getDeleteTaskUseCase = get(),
            convertChecklistIntoQuicklyChecklistUseCase = get(),
            getQuicklyChecklistDeepLinkUseCase = get()
        )
    }

}