package wottrich.github.io.quicklychecklist.impl.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import wottrich.github.io.quicklychecklist.impl.domain.ConvertChecklistIntoQuicklyChecklistUseCase
import wottrich.github.io.quicklychecklist.impl.domain.ConvertQuicklyChecklistIntoJsonUseCase
import wottrich.github.io.quicklychecklist.impl.domain.DecodeEncodedQuicklyChecklistUseCase
import wottrich.github.io.quicklychecklist.impl.domain.EncodeQuicklyChecklistUseCase
import wottrich.github.io.quicklychecklist.impl.domain.GetQuicklyChecklistDeepLinkUseCase
import wottrich.github.io.quicklychecklist.impl.presentation.viewmodels.InitialQuicklyChecklistViewModel
import wottrich.github.io.quicklychecklist.impl.presentation.viewmodels.QuicklyChecklistAddNewChecklistViewModel
import wottrich.github.io.quicklychecklist.impl.presentation.viewmodels.QuicklyChecklistConfirmViewModel
import wottrich.github.io.quicklychecklist.impl.presentation.viewmodels.QuicklyChecklistViewModel

val quicklyChecklistModule = module {
    factory { ConvertChecklistIntoQuicklyChecklistUseCase() }
    factory { ConvertQuicklyChecklistIntoJsonUseCase() }
    factory { DecodeEncodedQuicklyChecklistUseCase() }
    factory { EncodeQuicklyChecklistUseCase() }
    factory { GetQuicklyChecklistDeepLinkUseCase(get()) }
    viewModel { (encodedQuicklyChecklist: String?) ->
        InitialQuicklyChecklistViewModel(
            decodeEncodedQuicklyChecklistUseCase = get(),
            encodedQuicklyChecklist = encodedQuicklyChecklist
        )
    }
    viewModel { (quicklyChecklistJson: String) ->
        QuicklyChecklistViewModel(
            quicklyChecklistJson = quicklyChecklistJson,
            convertQuicklyChecklistIntoJsonUseCase = get()
        )
    }
    viewModel { (quicklyChecklistJson: String) ->
        QuicklyChecklistAddNewChecklistViewModel(get(), get(), quicklyChecklistJson)
    }
    viewModel { (quicklyChecklistJson: String) ->
        QuicklyChecklistConfirmViewModel(
            getQuicklyChecklistDeepLinkUseCase = get(),
            quicklyChecklistJson = quicklyChecklistJson
        )
    }
}