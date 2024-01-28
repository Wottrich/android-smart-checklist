package wottrich.github.io.smartchecklist.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.deletechecklist.viewmodel.DeleteChecklistBottomSheetViewModel
import wottrich.github.io.smartchecklist.domain.mapper.HomeDrawerChecklistItemModelMapper
import wottrich.github.io.smartchecklist.domain.mapper.SimpleChecklistModelMapper
import wottrich.github.io.smartchecklist.domain.usecase.GetChecklistDrawerUseCase
import wottrich.github.io.smartchecklist.domain.usecase.ObserveSimpleSelectedChecklistModelUseCase
import wottrich.github.io.smartchecklist.presentation.viewmodel.ChecklistSettingsViewModel
import wottrich.github.io.smartchecklist.presentation.viewmodel.HomeDrawerViewModel
import wottrich.github.io.smartchecklist.presentation.viewmodel.HomeViewModel

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
    viewModel {
        ChecklistSettingsViewModel(
            getSelectedChecklistUseCase = get(),
            shareChecklistAsTextUseCase = get(),
            convertChecklistIntoQuicklyChecklistUseCase = get(),
            getQuicklyChecklistDeepLinkUseCase = get()
        )
    }
    viewModelOf(::DeleteChecklistBottomSheetViewModel)
    viewModel { HomeDrawerViewModel(get(), get(), get(), get()) }
    viewModel {
        HomeViewModel(
            dispatchers = get(),
            observeSimpleSelectedChecklistModelUseCase = get(),
            deleteChecklistUseCase = get()
        )
    }
}