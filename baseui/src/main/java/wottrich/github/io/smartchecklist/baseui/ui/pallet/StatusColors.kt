package wottrich.github.io.smartchecklist.baseui.ui.pallet

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object ColorsLightStatusPallet {
    val positive = Color(0xFF589143)
    val negative = Color(0xFFB00020)
}

object ColorsDarkStatusPallet {
    val positive = Color(0xFF77E6A3)
    val negative = Color(0xFFFA6B6B)
}

class StatusColors(
    positive: Color,
    negative: Color,
    isLight: Boolean
) {

    var positive by mutableStateOf(positive)
        private set
    var negative by mutableStateOf(negative)
        private set
    var isLight by mutableStateOf(isLight)
        private set

    fun copy(
        positive: Color = this.positive,
        negative: Color = this.negative,
        isLight: Boolean = this.isLight,
    ): StatusColors = StatusColors(
        positive,
        negative,
        isLight
    )

    fun updateColorsFrom(other: StatusColors) {
        positive = other.positive
        negative = other.negative
        isLight = other.isLight
    }

}

fun statusColors(isLight: Boolean): StatusColors {
    return if (isLight) statusLightColors() else statusDarkColors()
}

fun statusLightColors(
    positive: Color = ColorsLightStatusPallet.positive,
    negative: Color = ColorsLightStatusPallet.negative
): StatusColors =
    StatusColors(
        positive,
        negative,
        isLight = true
    )

fun statusDarkColors(
    positive: Color = ColorsDarkStatusPallet.positive,
    negative: Color = ColorsDarkStatusPallet.negative
): StatusColors =
    StatusColors(
        positive,
        negative,
        isLight = false
    )