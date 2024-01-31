package wottrich.github.io.smartchecklist.baseui.icons

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme
import wottrich.github.io.smartchecklist.baseui.R

@Composable
fun IconCompletableContent(
    name: String,
    isCompleted: Boolean,
    onCheckChange: () -> Unit
) {

    val checkIconContentDescription = if (isCompleted) {
        R.string.item_completable_component_click_to_check_item_description
    } else {
        R.string.item_completable_component_click_to_uncheck_item_description
    }

    AnimatedContent(targetState = isCompleted, label = "IconCompletableTask$name") {
        val checkIcon = if (it) {
            R.drawable.ic_completed
        } else {
            R.drawable.ic_uncompleted
        }

        Icon(
            tint = SmartChecklistTheme.colors.status.positive,
            painter = painterResource(id = checkIcon),
            contentDescription = stringResource(checkIconContentDescription, name),
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onCheckChange() }
        )
    }

}