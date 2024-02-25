package wottrich.github.io.smartchecklist.quicklychecklist.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.quicklychecklist.domain.ConvertChecklistIntoQuicklyChecklistUseCase
import wottrich.github.io.smartchecklist.quicklychecklist.domain.ConvertQuicklyChecklistIntoJsonUseCase
import wottrich.github.io.smartchecklist.quicklychecklist.domain.DecodeEncodedQuicklyChecklistUseCase
import wottrich.github.io.smartchecklist.quicklychecklist.domain.EncodeQuicklyChecklistUseCase
import wottrich.github.io.smartchecklist.quicklychecklist.domain.GetQuicklyChecklistDeepLinkUseCase
import wottrich.github.io.smartchecklist.quicklychecklist.navigation.QuicklyChecklistContextNavigator
import wottrich.github.io.smartchecklist.quicklychecklist.presentation.viewmodels.InitialQuicklyChecklistViewModel
import wottrich.github.io.smartchecklist.quicklychecklist.presentation.viewmodels.QuicklyChecklistAddNewChecklistViewModel
import wottrich.github.io.smartchecklist.quicklychecklist.presentation.viewmodels.QuicklyChecklistConfirmViewModel
import wottrich.github.io.smartchecklist.quicklychecklist.presentation.viewmodels.QuicklyChecklistViewModel

val quicklyChecklistModule = module {
    single { QuicklyChecklistContextNavigator(get()) } bind SmartChecklistNavigation::class
    factory { ConvertChecklistIntoQuicklyChecklistUseCase(get()) }
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