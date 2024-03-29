package wottrich.github.io.smartchecklist.quicklychecklist.presentation.viewmodels

import kotlinx.coroutines.flow.Flow
import wottrich.github.io.smartchecklist.quicklychecklist.domain.GetQuicklyChecklistDeepLinkUseCase
import wottrich.github.io.smartchecklist.quicklychecklist.presentation.states.QuicklyChecklistConfirmUiEffect
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess

class QuicklyChecklistConfirmViewModel(
    private val getQuicklyChecklistDeepLinkUseCase: GetQuicklyChecklistDeepLinkUseCase,
    private val quicklyChecklistJson: String
) : BaseViewModel() {

    private val _effects = SingleShotEventBus<QuicklyChecklistConfirmUiEffect>()
    val effects: Flow<QuicklyChecklistConfirmUiEffect> = _effects.events

    fun onSaveNewChecklistClick() {
        launchMain {
            _effects.emit(
                QuicklyChecklistConfirmUiEffect.OnSaveNewChecklist(quicklyChecklistJson)
            )
        }
    }

    fun onShareChecklistBackClick() {
        handleGetQuicklyChecklistDeepLinkUseCase()
    }

    private fun handleGetQuicklyChecklistDeepLinkUseCase() {
        launchIO {
            getQuicklyChecklistDeepLinkUseCase(quicklyChecklistJson).onSuccess {
                emitShareChecklistBackEffect(it)
            }.onFailure {
                emitFailureShareChecklistBackEffect()
            }
        }
    }

    private suspend fun emitShareChecklistBackEffect(encodedQuicklyChecklist: String) {
        _effects.emit(
            QuicklyChecklistConfirmUiEffect.OnShareChecklistBack(encodedQuicklyChecklist)
        )
    }

    private suspend fun emitFailureShareChecklistBackEffect() {
        _effects.emit(QuicklyChecklistConfirmUiEffect.FailureShareChecklistBack)
    }
}