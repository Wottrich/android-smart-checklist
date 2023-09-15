package wottrich.github.io.smartchecklist.suggestion.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.entity.ChecklistTagsEmbeddedContract
import wottrich.github.io.smartchecklist.suggestion.domain.repository.SuggestionRepository

class GetChecklistTagsUseCaseImpl(
    private val repository: SuggestionRepository
) : GetChecklistTagsUseCase() {
    override suspend fun execute(params: String): Result<ChecklistTagsEmbeddedContract> {
        return Result.success(repository.getChecklistTags(params))
    }
}

abstract class GetChecklistTagsUseCase : KotlinResultUseCase<String, ChecklistTagsEmbeddedContract>()