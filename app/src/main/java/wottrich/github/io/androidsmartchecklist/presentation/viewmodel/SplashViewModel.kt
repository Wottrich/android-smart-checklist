package wottrich.github.io.androidsmartchecklist.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wottrich.github.io.tools.dispatcher.DispatchersProviders

class SplashViewModel(dispatchersProviders: DispatchersProviders) : ViewModel() {

    private val _continueState = mutableStateOf(false)
    val continueState: State<Boolean> = _continueState

    init {
        viewModelScope.launch(dispatchersProviders.io) {
            delay(SPLASH_DELAY)
            _continueState.value = true
        }
    }

    companion object {
        private const val SPLASH_DELAY = 1000L
    }

}