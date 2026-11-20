package wottrich.github.io.smartchecklist.share.presentation.state

sealed class NearbyUiEffects {
    data class ShowSnackbar(val textRes: Int) : NearbyUiEffects()
}
