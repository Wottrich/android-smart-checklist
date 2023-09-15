package wottrich.github.io.smartchecklist.baseui.previewparameters

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 10/08/23
 *
 * Copyright © 2023 AndroidSmartCheckList. All rights reserved.
 *
 */

class BooleanPreviewParameter : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(true, false)
}