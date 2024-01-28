package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.failureEmptyResult
import wottrich.github.io.smartchecklist.coroutines.successEmptyResult
import wottrich.github.io.smartchecklist.domain.model.SortItemType
import wottrich.github.io.smartchecklist.domain.repository.SortItemRepository

class SetSelectedSortItemUseCase(
    private val sortItemRepository: SortItemRepository
) : KotlinResultUseCase<SortItemType, UseCase.Empty>() {
    override suspend fun execute(params: SortItemType): Result<UseCase.Empty> {
        return try {
            sortItemRepository.setSortItem(params)
            successEmptyResult()
        } catch (ex: Exception) {
            failureEmptyResult(ex)
        }
    }
}