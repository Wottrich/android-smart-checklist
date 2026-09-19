package wottrich.github.io.smartchecklist.baseui.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistColors
import wottrich.github.io.smartchecklist.baseui.ui.pallet.darkColors
import wottrich.github.io.smartchecklist.baseui.ui.pallet.lightColors

@Composable
fun ApplicationTheme(
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme(),
    colors: SmartChecklistColors = getColorsBySystem(isSystemInDarkTheme),
    content: @Composable () -> Unit
) {
    val rememberedColors = remember {
        colors.copy()
    }.apply {
        updateColorsFrom(colors)
    }
    CompositionLocalProvider(
        LocalSmartChecklistColors provides rememberedColors,
    ) {
        MaterialTheme(
            colors = LocalSmartChecklistColors.current.toMaterialTheme(),
            content = content
        )
    }
}

fun SmartChecklistColors.toMaterialTheme(): Colors {
    return Colors(
        primary = primary,
        primaryVariant = primaryVariant,
        secondary = secondary,
        secondaryVariant = secondaryVariant,
        background = background,
        surface = surface,
        error = error,
        onPrimary = onPrimary,
        onSecondary = onSecondary,
        onBackground = onBackground,
        onSurface = onSurface,
        onError = onError,
        isLight = isLight
    )
}

@Composable
fun getColorsBySystem(isSystemInDarkTheme: Boolean): SmartChecklistColors {
    return if (isSystemInDarkTheme) {
        darkColors()
    } else {
        lightColors()
    }
}