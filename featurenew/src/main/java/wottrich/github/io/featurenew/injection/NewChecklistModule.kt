package wottrich.github.io.featurenew.injection

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import wottrich.github.io.featurenew.domain.usecase.GetAddTaskUseCase
import wottrich.github.io.featurenew.domain.usecase.GetChangeTaskStatusUseCase
import wottrich.github.io.featurenew.domain.usecase.GetDeleteChecklistUseCase
import wottrich.github.io.featurenew.domain.usecase.GetDeleteTaskUseCase
import wottrich.github.io.featurenew.domain.usecase.GetLoadTaskUseCase
import wottrich.github.io.featurenew.domain.usecase.GetSelectedChecklistUseCase
import wottrich.github.io.featurenew.domain.usecase.GetUpdateSelectedChecklistUseCase
import wottrich.github.io.featurenew.view.screens.checklistname.ChecklistNameViewModel
import wottrich.github.io.featurenew.view.screens.tasklist.TaskListViewModel

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
val featureNewModule = module {

    injectTaskUseCase()
    injectChecklistUseCase()

    viewModel { ChecklistNameViewModel(get(), get()) }
    viewModel<TaskListViewModel> { (checklistId: String) ->
        TaskListViewModel(checklistId, get(), get(), get(), get(), get())
    }

//    viewModel<ChecklistDetailViewModel> { (checklistId: String) ->
//        ChecklistDetailViewModel(get(), checklistId, get(), get())
//    }

}

fun Module.injectChecklistUseCase() {
    factory { GetDeleteChecklistUseCase(get()) }
    factory { GetSelectedChecklistUseCase(get()) }
    factory { GetUpdateSelectedChecklistUseCase(get()) }
}

fun Module.injectTaskUseCase() {
    factory { GetAddTaskUseCase(get()) }
    factory { GetDeleteTaskUseCase(get()) }
    factory { GetChangeTaskStatusUseCase(get()) }
    factory { GetLoadTaskUseCase(get()) }
}