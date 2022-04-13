package github.io.wottrich.ui.support.presentation.viewmodels

import github.io.wottrich.ui.support.data.HelpOverviewItem
import github.io.wottrich.ui.support.domain.GetHelpOverviewItemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.tools.base.BaseViewModel
import wottrich.github.io.tools.dispatcher.DispatchersProviders

class HelpOverviewViewModel(
    dispatchersProviders: DispatchersProviders,
    private val getHelpOverviewItemsUseCase: GetHelpOverviewItemsUseCase
) : BaseViewModel(dispatchersProviders) {

    private val _uiState = MutableStateFlow(HelpOverviewUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val items = getHelpOverviewItemsUseCase()
        _uiState.value = uiState.value.copy(helpItems = items)
    }
}

data class HelpOverviewUiState(
    val helpItems: List<HelpOverviewItem> = listOf()
)