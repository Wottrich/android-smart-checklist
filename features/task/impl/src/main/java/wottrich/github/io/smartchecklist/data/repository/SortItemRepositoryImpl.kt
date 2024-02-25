package wottrich.github.io.smartchecklist.data.repository

import kotlinx.coroutines.flow.Flow
import wottrich.github.io.smartchecklist.data.datasource.SortItemSource
import wottrich.github.io.smartchecklist.domain.model.SortItemType
import wottrich.github.io.smartchecklist.domain.repository.SortItemRepository

class SortItemRepositoryImpl(
    private val sortItemSource: SortItemSource
) : SortItemRepository {
    override fun observeSelectedSortItem(): Flow<SortItemType> {
        return sortItemSource.observableSelectedSortItem
    }

    override fun getSelectedSortItem(): SortItemType {
        return sortItemSource.getSelectedSortItem()
    }

    override suspend fun setSortItem(sortItemType: SortItemType) {
        sortItemSource.setSortItem(sortItemType)
    }

    override fun getSortItemTypeList(): List<SortItemType> {
        return sortItemSource.getSortItemTypeList()
    }
}