package wottrich.github.io.smartchecklist.baseui.components.checkbox

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import wottrich.github.io.smartchecklist.baseui.R
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

@Composable
fun IconCheckboxSelectorContent(
    label: String,
    isCompleted: Boolean,
    onCheckChange: () -> Unit
) {
    val checkIconContentDescription = if (isCompleted) {
        R.string.item_completable_component_click_to_check_item_description
    } else {
        R.string.item_completable_component_click_to_uncheck_item_description
    }

    AnimatedContent(targetState = isCompleted, label = "IconCompletableTask$label") {
        val checkIcon = if (it) {
            R.drawable.ic_check_square_rounded
        } else {
            R.drawable.ic_uncheck_square_rounded
        }

        Icon(
            tint = SmartChecklistTheme.colors.status.positive,
            painter = painterResource(id = checkIcon),
            contentDescription = stringResource(checkIconContentDescription, label),
            modifier = Modifier
                .clickable { onCheckChange() }
        )
    }
}