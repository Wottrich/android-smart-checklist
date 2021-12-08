package wottrich.github.io.baseui.ui.pallet

import androidx.compose.ui.graphics.Color

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 01/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

interface TextPallet {
    val primary: Color
    val secondary: Color
}

object TextLightPallet : TextPallet {
    override val primary = Color(0xFF2F2F33)
    override val secondary = Color(0xFF757680)
}

object TextDarkPallet : TextPallet {
    override val primary = Color(0xFFFFFFFF)
    override val secondary = Color(0xFF757680)
}