package wottrich.github.io.baseui.ui

import androidx.compose.runtime.compositionLocalOf
import wottrich.github.io.baseui.ui.color.LightColors
import wottrich.github.io.baseui.ui.pallet.TextLightPallet
import wottrich.github.io.baseui.ui.pallet.TextPallet

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 01/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

val LocalSmartChecklistColors = compositionLocalOf {
    LightColors
}

val LocalSmartChecklistTextColors = compositionLocalOf<TextPallet> {
    TextLightPallet
}
