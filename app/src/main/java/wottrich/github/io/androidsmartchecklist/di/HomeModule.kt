package wottrich.github.io.androidsmartchecklist.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import wottrich.github.io.androidsmartchecklist.domain.mapper.HomeDrawerChecklistItemModelMapper
import wottrich.github.io.androidsmartchecklist.domain.mapper.SimpleChecklistModelMapper
import wottrich.github.io.androidsmartchecklist.domain.usecase.GetChecklistDrawerUseCase
import wottrich.github.io.androidsmartchecklist.domain.usecase.ObserveSimpleSelectedChecklistModelUseCase
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.ChecklistSettingsViewModel
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerViewModel
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeViewModel
import wottrich.github.io.impl.presentation.viewmodel.TaskComponentViewModel

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 12/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
val featureHomeModules = module {
    factory { HomeDrawerChecklistItemModelMapper() }
    factory { SimpleChecklistModelMapper() }
    factory { GetChecklistDrawerUseCase(get(), get()) }
    factory { ObserveSimpleSelectedChecklistModelUseCase(get(), get()) }
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
            observeSimpleSelectedChecklistModelUseCase = get(),
            deleteChecklistUseCase = get(),
            convertChecklistIntoQuicklyChecklistUseCase = get(),
            getQuicklyChecklistDeepLinkUseCase = get(),
            getChecklistAsTextUseCase = get()
        )
    }
    viewModel {
        TaskComponentViewModel(
            getTasksFromSelectedChecklistUseCase = get(),
            addTaskToDatabaseUseCase = get(),
            getChangeTaskStatusUseCase = get(),
            getDeleteTaskUseCase = get()
        )
    }
}