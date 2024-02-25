package wottrich.github.io.smartchecklist.presentation.viewmodel

import kotlinx.coroutines.delay
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus
import wottrich.github.io.smartchecklist.android.BaseViewModel

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