package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.domain.repository.SortItemRepository
import wottrich.github.io.smartchecklist.presentation.ui.sort.SortItem

class GetSortItemListUseCase(
    private val sortItemRepository: SortItemRepository
) : KotlinResultUseCase<UseCase.None, List<SortItem>>() {
    override suspend fun execute(params: UseCase.None): Result<List<SortItem>> {
        return try {
            val result = sortItemRepository.getSortItemTypeList().map {
                SortItem(
                    it,
                    it.name == sortItemRepository.getSelectedSortItem().name
                )
            }
            Result.success(result)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}