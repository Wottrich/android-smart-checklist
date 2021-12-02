package wottrich.github.io.baseui.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import wottrich.github.io.baseui.ui.color.DarkColors
import wottrich.github.io.baseui.ui.color.LightColors
import wottrich.github.io.baseui.ui.pallet.TextDarkPallet
import wottrich.github.io.baseui.ui.pallet.TextLightPallet
import wottrich.github.io.baseui.ui.pallet.TextPallet

object SmartChecklistTheme {

    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalSmartChecklistColors.current

    val textColors: TextPallet
        @Composable
        @ReadOnlyComposable
        get() = LocalSmartChecklistTextColors.current

}

@Composable
fun ApplicationTheme(
    colors: Colors = getColorsBySystem(),
    textColors: TextPallet = getTextColorsBySystem(),
    content: @Composable () -> Unit
) {

    val rememberedColors = remember {
        colors.copy()
    }
    CompositionLocalProvider(
        LocalSmartChecklistColors provides rememberedColors,
        LocalSmartChecklistTextColors provides textColors
    ) {
        MaterialTheme(
            colors = LocalSmartChecklistColors.current,
            content = content
        )
    }
}

@Composable
fun getColorsBySystem(): Colors {
    return if (isSystemInDarkTheme()) {
        DarkColors
    } else {
        LightColors
    }
}

@Composable
fun getTextColorsBySystem(): TextPallet {
    return if(isSystemInDarkTheme()) {
        TextDarkPallet
    } else {
        TextLightPallet
    }
}