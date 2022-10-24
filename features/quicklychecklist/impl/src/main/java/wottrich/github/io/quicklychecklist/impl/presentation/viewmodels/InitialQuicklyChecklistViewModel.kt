package wottrich.github.io.quicklychecklist.impl.presentation.viewmodels

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.Flow
import wottrich.github.io.quicklychecklist.impl.presentation.states.InitialQuicklyChecklistUiEffect
import wottrich.github.io.tools.SingleShotEventBus
import wottrich.github.io.tools.base.BaseViewModel

class InitialQuicklyChecklistViewModel : BaseViewModel() {

    var quicklyChecklistJson by mutableStateOf("")
        private set

    val isButtonEnabled by derivedStateOf {
        quicklyChecklistJson.isNotEmpty()
    }

    private val _effects = SingleShotEventBus<InitialQuicklyChecklistUiEffect>()
    val effects: Flow<InitialQuicklyChecklistUiEffect> = _effects.events

    fun onQuicklyChecklistJsonChange(quicklyChecklistJson: String) {
        this.quicklyChecklistJson = quicklyChecklistJson
    }

    fun onConfirmButtonClicked() {
        launchMain {
            _effects.emit(
                InitialQuicklyChecklistUiEffect.OnConfirmButtonClicked(quicklyChecklistJson)
            )
        }
    }

    fun onInvalidChecklist() {
        launchMain {
            _effects.emit(
                InitialQuicklyChecklistUiEffect.OnInvalidChecklist
            )
        }
    }


}