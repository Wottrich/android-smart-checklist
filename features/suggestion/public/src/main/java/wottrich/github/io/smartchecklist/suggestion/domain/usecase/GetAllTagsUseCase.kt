package wottrich.github.io.smartchecklist.suggestion.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.suggestion.TagContract
import wottrich.github.io.smartchecklist.suggestion.TagSuggestionEmbeddedContract

abstract class GetAllTagsUseCase : KotlinResultUseCase<UseCase.None, List<TagSuggestionEmbeddedContract>>()