package wottrich.github.io.quicklychecklist.impl.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import wottrich.github.io.quicklychecklist.impl.domain.ConvertChecklistIntoQuicklyChecklistUseCase
import wottrich.github.io.quicklychecklist.impl.presentation.viewmodels.InitialQuicklyChecklistViewModel
import wottrich.github.io.quicklychecklist.impl.presentation.viewmodels.QuicklyChecklistViewModel

val quicklyChecklistModule = module {
    factory { ConvertChecklistIntoQuicklyChecklistUseCase() }
    viewModel { InitialQuicklyChecklistViewModel() }
    viewModel { (quicklyChecklistJson: String) ->
        QuicklyChecklistViewModel(
            quicklyChecklistJson = quicklyChecklistJson
        )
    }
}