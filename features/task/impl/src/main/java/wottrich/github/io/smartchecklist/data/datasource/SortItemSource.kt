package wottrich.github.io.smartchecklist.data.datasource

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.smartchecklist.domain.model.SortItemType

object SortItemSource {

    private var selectedSortItem = SortItemType.UNSELECTED_SORT

    private val _observableSelectedSortItem: MutableStateFlow<SortItemType> = MutableStateFlow(
        selectedSortItem
    )
    val observableSelectedSortItem = _observableSelectedSortItem.asStateFlow()

    fun getSelectedSortItem() = selectedSortItem

    suspend fun setSortItem(sortItemType: SortItemType) {
        selectedSortItem = sortItemType
        _observableSelectedSortItem.emit(sortItemType)
    }

    fun getSortItemTypeList() = SortItemType.entries

}