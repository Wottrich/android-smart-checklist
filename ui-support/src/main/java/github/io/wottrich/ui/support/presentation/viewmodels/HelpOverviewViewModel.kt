package github.io.wottrich.ui.support.presentation.viewmodels

import github.io.wottrich.ui.support.data.HelpOverviewItem
import github.io.wottrich.ui.support.domain.GetHelpOverviewItemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders

class HelpOverviewViewModel(
    dispatchersProviders: DispatchersProviders,
    private val getHelpOverviewItemsUseCase: GetHelpOverviewItemsUseCase
) : BaseViewModel(dispatchersProviders) {

    private val _uiState = MutableStateFlow(HelpOverviewUiState())
    val uiState = _uiState.asStateFlow()

    init {
        launchIO {
            getHelpOverviewItemsUseCase().onSuccess { items ->
                _uiState.value = uiState.value.copy(helpItems = items)
            }
        }
    }
}

data class HelpOverviewUiState(
    val helpItems: List<HelpOverviewItem> = listOf()
)