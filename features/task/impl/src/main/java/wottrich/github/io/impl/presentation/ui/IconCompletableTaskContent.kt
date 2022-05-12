package wottrich.github.io.impl.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import wottrich.github.io.impl.R
import wottrich.github.io.impl.R.drawable

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
            drawable.ic_completed
        } else {
            drawable.ic_uncompleted
        }

        Icon(
            tint = MaterialTheme.colors.secondary,
            painter = painterResource(id = checkIcon),
            contentDescription = stringResource(checkIconContentDescription, taskName),
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onCheckChange() }
        )
    }

}