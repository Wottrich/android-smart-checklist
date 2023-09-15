package wottrich.github.io.smartchecklist.suggestion.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.suggestion.TagContract
import wottrich.github.io.smartchecklist.suggestion.TagSuggestionEmbeddedContract
import wottrich.github.io.smartchecklist.suggestion.domain.repository.SuggestionRepository

class GetAllTagsUseCaseImpl(
    private val suggestionRepository: SuggestionRepository
) : GetAllTagsUseCase() {
    override suspend fun execute(params: UseCase.None): Result<List<TagSuggestionEmbeddedContract>> {
        return Result.success(suggestionRepository.getAllTagsWithSuggestions())
    }
}