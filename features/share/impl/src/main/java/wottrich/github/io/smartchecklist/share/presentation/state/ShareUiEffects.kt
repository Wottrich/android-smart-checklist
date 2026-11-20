package wottrich.github.io.smartchecklist.share.presentation.state

sealed class ShareUiEffects {
    data class ShowSnackbar(val textRes: Int) : ShareUiEffects()
}
