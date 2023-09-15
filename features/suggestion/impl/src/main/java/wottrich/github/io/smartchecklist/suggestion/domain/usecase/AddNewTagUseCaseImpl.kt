package wottrich.github.io.smartchecklist.suggestion.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.failureEmptyResult
import wottrich.github.io.smartchecklist.coroutines.successEmptyResult
import wottrich.github.io.smartchecklist.datasource.entity.TagEntity
import wottrich.github.io.smartchecklist.suggestion.domain.repository.SuggestionRepository

class AddNewTagUseCaseImpl(
    private val repository: SuggestionRepository
) : AddNewTagUseCase() {
    override suspend fun execute(params: String): Result<UseCase.Empty> {
        return try {
            repository.addNewTag(TagEntity(name = params))
            successEmptyResult()
        } catch (ex: Exception) {
            failureEmptyResult(ex)
        }
    }
}