package wottrich.github.io.quicklychecklist.impl.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import wottrich.github.io.baseui.ui.pallet.SmartChecklistTheme
import wottrich.github.io.impl.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun IconCompletableTaskContent(
    taskName: String,
    isCompletedTask: Boolean,
    onCheckChange: () -> Unit
) {

    val checkIconContentDescription = if (isCompletedTask) {
        R.string.task_item_component_click_to_uncheck_item_description
    } else {
        R.string.task_item_component_click_to_check_item_description
    }

    AnimatedContent(targetState = isCompletedTask) {
        val checkIcon = if (it) {
            R.drawable.ic_completed
        } else {
            R.drawable.ic_uncompleted
        }

        Icon(
            tint = SmartChecklistTheme.colors.status.positive,
            painter = painterResource(id = checkIcon),
            contentDescription = stringResource(checkIconContentDescription, taskName),
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onCheckChange() }
        )
    }

}