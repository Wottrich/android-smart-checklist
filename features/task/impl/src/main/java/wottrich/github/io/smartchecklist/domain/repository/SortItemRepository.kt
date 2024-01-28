package wottrich.github.io.smartchecklist.domain.repository

import kotlinx.coroutines.flow.Flow
import wottrich.github.io.smartchecklist.domain.model.SortItemType

interface SortItemRepository {
    fun observeSelectedSortItem(): Flow<SortItemType>
    fun getSelectedSortItem(): SortItemType
    suspend fun setSortItem(sortItemType: SortItemType)
    fun getSortItemTypeList(): List<SortItemType>
}