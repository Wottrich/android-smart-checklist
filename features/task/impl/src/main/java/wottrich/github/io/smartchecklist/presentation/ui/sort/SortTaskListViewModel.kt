package wottrich.github.io.smartchecklist.presentation.ui.sort

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.domain.usecase.GetSortItemListUseCase
import wottrich.github.io.smartchecklist.domain.usecase.SetSelectedSortItemUseCase
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus

class SortTaskListViewModel(
    private val getSortItemListUseCase: GetSortItemListUseCase,
    private val setSelectedSortItemUseCase: SetSelectedSortItemUseCase
) : BaseViewModel(), SortTaskListAction {

    val sortItems = mutableStateListOf<SortItem>()

    private val _uiEffect = SingleShotEventBus<SortTaskListUiEffect>()
    val uiEffect: Flow<SortTaskListUiEffect> = _uiEffect.events

    override fun sendAction(action: SortTaskListAction.Action) {
        when (action) {
            is SortTaskListAction.Action.OnSelectedItem -> onSelectedItem(action.sortItem)
        }
    }

    init {
        launchIO {
            getSortItemListUseCase().onSuccess {
                withContext(main()) {
                    sortItems.clear()
                    sortItems.addAll(it)
                }
            }
        }
    }

    private fun onSelectedItem(sortItem: SortItem) {
        launchIO {
            setSelectedSortItemUseCase(sortItem.sortItemType).onSuccess {
                _uiEffect.emit(SortTaskListUiEffect.CloseBottomSheet)
            }.onFailure {
                _uiEffect.emit(SortTaskListUiEffect.SetSortFailureSnackbar)
            }
        }
    }
}