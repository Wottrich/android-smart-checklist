package wottrich.github.io.baseui.ui.pallet

import androidx.compose.ui.graphics.Color

fun lightColors(
    primary: Color = ColorsLightPallet.primary,
    primaryVariant: Color = ColorsLightPallet.primaryVariant,
    secondary: Color = ColorsLightPallet.secondary,
    secondaryVariant: Color = ColorsLightPallet.secondaryVariant,
    background: Color = ColorsLightPallet.background,
    surface: Color = ColorsLightPallet.surface,
    error: Color = ColorsLightPallet.error,
    onPrimary: Color = ColorsLightPallet.onPrimary,
    onSecondary: Color = ColorsLightPallet.onSecondary,
    onBackground: Color = ColorsLightPallet.onBackground,
    onSurface: Color = ColorsLightPallet.onSurface,
    status: StatusColors = statusColors(true),
    onError: Color = ColorsLightPallet.onError
): SmartChecklistColors = SmartChecklistColors(
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
    status,
    isLight = true
)

fun darkColors(
    primary: Color = ColorsDarkPallet.primary,
    primaryVariant: Color = ColorsDarkPallet.primaryVariant,
    secondary: Color = ColorsDarkPallet.secondary,
    secondaryVariant: Color = ColorsDarkPallet.secondaryVariant,
    background: Color = ColorsDarkPallet.background,
    surface: Color = ColorsDarkPallet.surface,
    error: Color = ColorsDarkPallet.error,
    onPrimary: Color = ColorsDarkPallet.onPrimary,
    onSecondary: Color = ColorsDarkPallet.onSecondary,
    onBackground: Color = ColorsDarkPallet.onBackground,
    onSurface: Color = ColorsDarkPallet.onSurface,
    status: StatusColors = statusColors(false),
    onError: Color = ColorsDarkPallet.onError
): SmartChecklistColors = SmartChecklistColors(
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
    status,
    isLight = false
)