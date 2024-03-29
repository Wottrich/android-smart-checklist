package wottrich.github.io.smartchecklist.quicklychecklist.presentation.viewmodels

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import kotlinx.coroutines.flow.Flow
import wottrich.github.io.smartchecklist.quicklychecklist.domain.DecodeEncodedQuicklyChecklistUseCase
import wottrich.github.io.smartchecklist.quicklychecklist.presentation.states.InitialQuicklyChecklistUiEffect
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus
import wottrich.github.io.smartchecklist.android.BaseViewModel

class InitialQuicklyChecklistViewModel(
    private val decodeEncodedQuicklyChecklistUseCase: DecodeEncodedQuicklyChecklistUseCase,
    private val encodedQuicklyChecklist: String?
) : BaseViewModel() {

    var quicklyChecklistJson by mutableStateOf("")
        private set

    val isButtonEnabled by derivedStateOf {
        quicklyChecklistJson.isNotEmpty()
    }

    val isChecklistReceived by derivedStateOf {
        quicklyChecklistJson.isNotEmpty()
    }

    private val _effects = SingleShotEventBus<InitialQuicklyChecklistUiEffect>()
    val effects: Flow<InitialQuicklyChecklistUiEffect> = _effects.events

    init {
        decodeEncodedQuicklyChecklistIfNotNull()
    }

    fun onQuicklyChecklistJsonChange(quicklyChecklistJson: String) {
        this.quicklyChecklistJson = quicklyChecklistJson
    }

    fun onConfirmButtonClicked() {
        handleQuicklyChecklistJsonToNextScreen()
    }

    fun onInvalidChecklist() {
        handleInvalidChecklistEffect()
    }

    private fun decodeEncodedQuicklyChecklistIfNotNull() {
        if (encodedQuicklyChecklist != null) {
            launchIO {
                decodeEncodedQuicklyChecklistUseCase(encodedQuicklyChecklist).onSuccess {
                    quicklyChecklistJson = it
                    handleQuicklyChecklistJsonToNextScreen()
                }.onFailure {
                    handleInvalidChecklistEffect()
                }
            }
        }
    }

    private fun handleQuicklyChecklistJsonToNextScreen() {
        launchMain {
            _effects.emit(
                InitialQuicklyChecklistUiEffect.OnQuicklyChecklistJson(quicklyChecklistJson)
            )
        }
    }

    private fun handleInvalidChecklistEffect() {
        launchMain {
            _effects.emit(
                InitialQuicklyChecklistUiEffect.OnInvalidChecklist
            )
        }
    }
}