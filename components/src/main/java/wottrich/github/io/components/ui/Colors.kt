package wottrich.github.io.components.ui

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

interface Pallet {
    val primary: Color
    val primaryVariant: Color
    val secondary: Color
    val secondaryVariant: Color
    val background: Color
    val onPrimary: Color
    val surface: Color
    val onSurface: Color

    fun build(): Colors
}

object LightPallet : Pallet {
    override val primary = Color(0xFFECEFF1)
    override val primaryVariant = Color(0xFFBABDBE)
    override val secondary = Color(0xFF589143)
    override val secondaryVariant = Color(0xFF88C270)
    override val background = primary
    override val onPrimary = Color(0xFF4F5B62)
    override val surface = Color(0xFFE8EBEE)
    override val onSurface = onPrimary

    override fun build() = lightColors(
        primary = primary,
        primaryVariant = primary,
        secondary = secondary,
        secondaryVariant = secondaryVariant,
        background = background,
        onPrimary = onPrimary,
        surface = surface,
        onSurface = onSurface
    )
}

object DarkPallet : Pallet {
    override val primary = Color(0xFF263238)
    override val primaryVariant = Color(0xFF000A12)
    override val secondary = Color(0xFF62BC86)
    override val secondaryVariant = Color(0xFF94EFB6)
    override val background = primary
    override val onPrimary = Color(0xFFECEFF1)
    override val surface = Color(0xFF192124)
    override val onSurface = onPrimary

    override fun build() = darkColors(
        primary = primary,
        primaryVariant = primary,
        secondary = secondary,
        secondaryVariant = secondaryVariant,
        background = background,
        onPrimary = onPrimary,
        surface = surface,
        onSurface = onSurface
    )
}