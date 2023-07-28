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
    colors: SmartChecklistColors = getColorsBySystem(),
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
        primary,
        primaryVariant,
        secondary,
        secondaryVariant,
        background,
        surface,
        error,
        onPrimary,
        onSecondary,
        onBackground,
        onSurface,
        onError,
        isLight
    )
}

@Composable
fun getColorsBySystem(): SmartChecklistColors {
    return if (isSystemInDarkTheme()) {
        darkColors()
    } else {
        lightColors()
    }
}