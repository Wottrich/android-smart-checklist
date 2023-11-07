package wottrich.github.io.smartchecklist.presentation.ui.checklistinformationheader

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.RowDefaults
import wottrich.github.io.smartchecklist.baseui.ui.TextStateComponent
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

@Composable
fun CompletableCountComponent(
    completedTasks: Int,
    onTaskCounterClicked: (() -> Unit)? = null
) {
    AnimatedUncompletedTasksChangeComponent(
        completedTasksCount = completedTasks,
        onTaskCounterClicked = onTaskCounterClicked
    )
}

@Composable
private fun AnimatedUncompletedTasksChangeComponent(
    completedTasksCount: Int,
    onTaskCounterClicked: (() -> Unit)? = null
) {
    AnimatedUncompletedTasksChangeSurface(onTaskCounterClicked) {
        AnimatedContent(
            targetState = completedTasksCount,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically { height -> height } + fadeIn() togetherWith
                        slideOutVertically { height -> -height } + fadeOut()
                } else {
                    slideInVertically { height -> -height } + fadeIn() togetherWith
                        slideOutVertically { height -> height } + fadeOut()
                }.using(
                    SizeTransform(clip = false)
                )
            },
            label = "UncompletedTaskChangeAnimation"
        ) {
            TextStateComponent(
                textState = RowDefaults.description(
                    text = it.toString()
                )
            )
        }
    }
}

@Composable
private fun AnimatedUncompletedTasksChangeSurface(
    onTaskCounterClicked: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(Dimens.BaseFour.SizeTwo)
            .size(Dimens.BaseFour.SizeNine),
        shape = RoundedCornerShape(Dimens.BaseFour.SizeTwo),
        color = MaterialTheme.colors.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = onTaskCounterClicked != null) { onTaskCounterClicked?.invoke() },

            contentAlignment = Alignment.Center
        ) {
//  TODO review before apply AnimateChangeCompletedTaskLayer(completedTasksCount)
            content()
        }
    }
}

@Composable
private fun AnimateChangeCompletedTaskLayer(completedTasksCount: Int) {
    val positiveColor = SmartChecklistTheme.colors.status.positive
    val negativeColor = SmartChecklistTheme.colors.status.negative
    val surfaceColor = SmartChecklistTheme.colors.surface
    val firstTime = rememberSaveable { mutableStateOf(true) }
    val count = remember {
        mutableIntStateOf(completedTasksCount)
    }
    val color = remember(completedTasksCount) {
        if (firstTime.value) {
            firstTime.value = false
            return@remember mutableStateOf(surfaceColor)
        }
        val color = if (count.intValue < completedTasksCount) positiveColor
        else negativeColor
        count.intValue = completedTasksCount
        mutableStateOf(color)
    }

    val animatedColor = animateColorAsState(
        targetValue = color.value,
        animationSpec = tween(250),
        label = "AnimateChangeText"
    ) {
        if (color.value != surfaceColor) {
            color.value = surfaceColor
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedColor.value)
    )
}

@Preview
@Composable
private fun AnimatedUncompletedTasksChangeComponentPreview() {
    val count = remember { mutableIntStateOf(0) }
    ApplicationTheme {
        Surface {
            Column {
                Button(onClick = { count.intValue = count.intValue + 1 }) {
                    Text(text = "Increase count")
                }

                Button(onClick = { count.intValue = count.intValue - 1 }) {
                    Text(text = "Decrease count")
                }
                AnimatedUncompletedTasksChangeComponent(
                    completedTasksCount = count.intValue,
                ) {}
            }
        }
    }
}