package wottrich.github.io.androidsmartchecklist.presentation.viewmodel

import kotlinx.coroutines.delay
import github.io.wottrich.kotlin.SingleShotEventBus
import github.io.wottrich.android.BaseViewModel

class SplashViewModel: BaseViewModel() {

    private val _uiEffect = SingleShotEventBus<SplashUiEffect>()
    val uiEffect = _uiEffect.events

    init {
        launchIO {
            delay(SPLASH_DELAY)
            _uiEffect.emit(SplashUiEffect.GoToHome)
        }
    }

    companion object {
        private const val SPLASH_DELAY = 1000L
    }
}

sealed class SplashUiEffect {
    object GoToHome : SplashUiEffect()
}