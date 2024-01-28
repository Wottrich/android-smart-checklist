package wottrich.github.io.smartchecklist.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import wottrich.github.io.smartchecklist.coroutines.FlowableUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.domain.model.SortItemType
import wottrich.github.io.smartchecklist.domain.repository.SortItemRepository

class ObserveSortItemSelectedUseCase(
    private val sortItemRepository: SortItemRepository
) : FlowableUseCase<UseCase.None, SortItemType>() {
    override suspend fun execute(params: UseCase.None): Flow<Result<SortItemType>> {
        return sortItemRepository.observeSelectedSortItem().map {
            Result.success(it)
        }.distinctUntilChanged()
    }
}