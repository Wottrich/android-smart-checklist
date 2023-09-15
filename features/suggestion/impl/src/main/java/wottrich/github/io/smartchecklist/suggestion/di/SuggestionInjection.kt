package wottrich.github.io.smartchecklist.suggestion.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.suggestion.data.repository.SuggestionRepositoryImpl
import wottrich.github.io.smartchecklist.suggestion.domain.repository.SuggestionRepository
import wottrich.github.io.smartchecklist.suggestion.domain.usecase.AddNewTagUseCase
import wottrich.github.io.smartchecklist.suggestion.domain.usecase.AddNewTagUseCaseImpl
import wottrich.github.io.smartchecklist.suggestion.domain.usecase.GetAllTagsUseCase
import wottrich.github.io.smartchecklist.suggestion.domain.usecase.GetAllTagsUseCaseImpl
import wottrich.github.io.smartchecklist.suggestion.domain.usecase.GetChecklistTagsUseCase
import wottrich.github.io.smartchecklist.suggestion.domain.usecase.GetChecklistTagsUseCaseImpl
import wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagregister.RegisterNewTagWithSuggestionsViewModel
import wottrich.github.io.smartchecklist.suggestion.presentation.viewmodel.ChecklistTagsViewModel
import wottrich.github.io.smartchecklist.suggestion.presentation.viewmodel.TagsOverviewViewModel

val suggestionModule = module {
    factory<SuggestionRepository> { SuggestionRepositoryImpl(get()) }
    factory<AddNewTagUseCase> { AddNewTagUseCaseImpl(get()) }
    factory<GetAllTagsUseCase> { GetAllTagsUseCaseImpl(get()) }
    factory<GetChecklistTagsUseCase> { GetChecklistTagsUseCaseImpl(get()) }
    viewModel { (checklistUuid: String) ->
        ChecklistTagsViewModel(checklistUuid, get(), get())
    }
    viewModel { TagsOverviewViewModel(get()) }
    viewModel { RegisterNewTagWithSuggestionsViewModel() }
}