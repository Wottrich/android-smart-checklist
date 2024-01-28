package wottrich.github.io.smartchecklist.baseui.ui.pallet

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object ColorsLightStatusPallet {
    val positive = Color(0xFF589143)
    val onPositive = Color(0xFFFFFFFF)
    val negative = Color(0xFFB00020)
    val onNegative = Color(0xFFFFFFFF)
}

object ColorsDarkStatusPallet {
    val positive = Color(0xFF77E6A3)
    val onPositive = Color(0xFF000A12)
    val negative = Color(0xFFFA6B6B)
    val onNegative = Color(0xFF000A12)
}

class StatusColors(
    positive: Color,
    onPositive: Color,
    negative: Color,
    onNegative: Color,
    isLight: Boolean
) {

    var positive by mutableStateOf(positive)
        private set
    var onPositive by mutableStateOf(onPositive)
        private set
    var negative by mutableStateOf(negative)
        private set
    var onNegative by mutableStateOf(onNegative)
    var isLight by mutableStateOf(isLight)
        private set

    fun copy(
        positive: Color = this.positive,
        onPositive: Color = this.onPositive,
        negative: Color = this.negative,
        onNegative: Color = this.onNegative,
        isLight: Boolean = this.isLight,
    ): StatusColors = StatusColors(
        positive,
        onPositive,
        negative,
        onNegative,
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
    onPositive: Color = ColorsLightStatusPallet.onPositive,
    negative: Color = ColorsLightStatusPallet.negative,
    onNegative: Color = ColorsLightStatusPallet.onNegative
): StatusColors =
    StatusColors(
        positive,
        onPositive,
        negative,
        onNegative,
        isLight = true
    )

fun statusDarkColors(
    positive: Color = ColorsDarkStatusPallet.positive,
    onPositive: Color = ColorsDarkStatusPallet.onPositive,
    negative: Color = ColorsDarkStatusPallet.negative,
    onNegative: Color = ColorsDarkStatusPallet.onNegative
): StatusColors =
    StatusColors(
        positive,
        onPositive,
        negative,
        onNegative,
        isLight = false
    )