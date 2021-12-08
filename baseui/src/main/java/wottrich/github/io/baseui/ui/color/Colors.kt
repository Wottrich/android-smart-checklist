package wottrich.github.io.baseui.ui.color

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import wottrich.github.io.baseui.ui.pallet.ColorsDarkPallet
import wottrich.github.io.baseui.ui.pallet.ColorsLightPallet

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 30/11/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */
 
val LightColors = lightColors(
    primary = ColorsLightPallet.primary,
    primaryVariant = ColorsLightPallet.primaryVariant,
    secondary = ColorsLightPallet.secondary,
    secondaryVariant = ColorsLightPallet.secondaryVariant,
    background = ColorsLightPallet.background,
    surface = ColorsLightPallet.surface,
    error = ColorsLightPallet.error,
    onPrimary = ColorsLightPallet.onPrimary,
    onSecondary = ColorsLightPallet.onSecondary,
    onBackground = ColorsLightPallet.onBackground,
    onSurface = ColorsLightPallet.onSurface,
    onError = ColorsLightPallet.onError
)

val DarkColors = darkColors(
    primary = ColorsDarkPallet.primary,
    primaryVariant = ColorsDarkPallet.primaryVariant,
    secondary = ColorsDarkPallet.secondary,
    secondaryVariant = ColorsDarkPallet.secondaryVariant,
    background = ColorsDarkPallet.background,
    surface = ColorsDarkPallet.surface,
    error = ColorsDarkPallet.error,
    onPrimary = ColorsDarkPallet.onPrimary,
    onSecondary = ColorsDarkPallet.onSecondary,
    onBackground = ColorsDarkPallet.onBackground,
    onSurface = ColorsDarkPallet.onSurface,
    onError = ColorsDarkPallet.onError
)

val ColorsDefaults: Colors
    @Composable @ReadOnlyComposable get() = if(isSystemInDarkTheme()) {
        Colors(
            primary = ColorsDarkPallet.primary,
            primaryVariant = ColorsDarkPallet.primaryVariant,
            secondary = ColorsDarkPallet.secondary,
            secondaryVariant = ColorsDarkPallet.secondaryVariant,
            background = ColorsDarkPallet.background,
            surface = ColorsDarkPallet.surface,
            error = ColorsDarkPallet.error,
            onPrimary = ColorsDarkPallet.onPrimary,
            onSecondary = ColorsDarkPallet.onSecondary,
            onBackground = ColorsDarkPallet.onBackground,
            onSurface = ColorsDarkPallet.onSurface,
            onError = ColorsDarkPallet.onError,
            isLight = false
        )
    } else {
        Colors(
            primary = ColorsLightPallet.primary,
            primaryVariant = ColorsLightPallet.primaryVariant,
            secondary = ColorsLightPallet.secondary,
            secondaryVariant = ColorsLightPallet.secondaryVariant,
            background = ColorsLightPallet.background,
            surface = ColorsLightPallet.surface,
            error = ColorsLightPallet.error,
            onPrimary = ColorsLightPallet.onPrimary,
            onSecondary = ColorsLightPallet.onSecondary,
            onBackground = ColorsLightPallet.onBackground,
            onSurface = ColorsLightPallet.onSurface,
            onError = ColorsLightPallet.onError,
            isLight = true
        )
    }