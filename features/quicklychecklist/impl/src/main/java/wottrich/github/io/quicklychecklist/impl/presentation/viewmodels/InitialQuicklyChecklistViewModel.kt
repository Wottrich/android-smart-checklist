package wottrich.github.io.quicklychecklist.impl.presentation.viewmodels

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import wottrich.github.io.tools.base.BaseViewModel

class InitialQuicklyChecklistViewModel : BaseViewModel() {

    var quicklyChecklistJson by mutableStateOf("")
        private set

    val isButtonEnabled by derivedStateOf {
        quicklyChecklistJson.isNotEmpty()
    }

    fun onQuicklyChecklistJsonChange(quicklyChecklistJson: String) {
        this.quicklyChecklistJson = quicklyChecklistJson
    }

    fun onConfirmButtonClicked() {
        // call effect here
    }


}