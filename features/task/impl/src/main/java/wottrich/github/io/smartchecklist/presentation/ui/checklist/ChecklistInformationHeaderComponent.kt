package wottrich.github.io.smartchecklist.presentation.ui.checklist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.RowDefaults
import wottrich.github.io.smartchecklist.baseui.ui.TextStateComponent
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme
import wottrich.github.io.smartchecklist.datasource.entity.NewTask
import kotlin.random.Random

@Composable
fun ChecklistInformationHeaderComponent(
    checklistName: String,
    completedTasksCount: Int,
    totalTasksCount: Int
) {
    val itemWidth = remember { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = Dimens.BaseFour.SizeThree,
                horizontal = Dimens.BaseFour.SizeThree
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged {
                    itemWidth.intValue = it.width
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextStateComponent(
                textState = RowDefaults.text(
                    checklistName,
                    style = MaterialTheme.typography.h5
                )
            )
            AnimatedUncompletedTasksChangeComponent(completedTasksCount)
        }
        CompletableProgressComponent(
            itemWidth.intValue.toFloat(),
            completedTasksCount,
            totalTasksCount
        )
    }
}

@Composable
private fun AnimatedUncompletedTasksChangeComponent(completedTasksCount: Int) {
    AnimatedUncompletedTasksChangeSurface(completedTasksCount) {
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
    completedTasksCount: Int,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(Dimens.BaseFour.SizeTwo)
            .size(Dimens.BaseFour.SizeNine)
            .clickable { /*TODO apply tooltip here!*/ },
        shape = RoundedCornerShape(Dimens.BaseFour.SizeTwo),
        color = MaterialTheme.colors.surface
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//  TODO review before apply AnimateChangeCompletedTaskLayer(completedTasksCount)
            content()
        }
    }
}

@Composable
fun AnimateChangeCompletedTaskLayer(completedTasksCount: Int) {
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

@Composable
private fun CompletableProgressComponent(
    width: Float,
    completedTasksCount: Int,
    totalTasksCount: Int
) {
    val backgroundColor = SmartChecklistTheme.colors.surface
    val progress = calculateCurrentWidth(completedTasksCount, totalTasksCount)
    val animateProgress by animateFloatAsState(
        targetValue = progress,
        label = "TaskInformationProgressValueAnimation"
    )
    val progressColor = getAnimatedProgressColor(progress)
    Box(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier.height(Dimens.BaseFour.SizeTwo),
            onDraw = {
                drawRoundRect(
                    color = backgroundColor,
                    topLeft = Offset(
                        x = width,
                        y = 0f
                    ),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                drawRoundRect(
                    color = progressColor,
                    topLeft = Offset(
                        x = width * animateProgress,
                        y = 0f
                    ),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }
        )
    }
}

@Composable
fun getAnimatedProgressColor(progress: Float): Color {
    val color = if (progress <= 0.5f) SmartChecklistTheme.colors.status.negative
    else SmartChecklistTheme.colors.status.positive
    val animatedColor by animateColorAsState(
        targetValue = color,
        label = "TaskInformationProgressColorAnimation"
    )
    return animatedColor
}

private fun calculateCurrentWidth(
    completedTasksCount: Int,
    totalTasksCount: Int,
): Float {
    return completedTasksCount.toFloat() / totalTasksCount.toFloat()
}

@Preview
@Composable
private fun TaskInformationHeaderComponentPreview() {
    ApplicationTheme {
        val zerar = remember { mutableStateOf(false) }
        val tasks = remember(zerar.value) {
            mutableStateListOf(
                NewTask(
                    parentUuid = "123",
                    name = "Task name",
                    isCompleted = false
                ),
                NewTask(
                    parentUuid = "123",
                    name = "Task name",
                    isCompleted = false
                ),
                NewTask(
                    parentUuid = "123",
                    name = "Task name",
                    isCompleted = true
                )
            )
        }
        Column(
            modifier = Modifier
                .background(SmartChecklistTheme.colors.background)
                .fillMaxHeight()
        ) {
            Row {
                Button(onClick = {
                    tasks.add(
                        NewTask(
                            parentUuid = "123",
                            name = "Task: ${tasks.size}",
                            isCompleted = true
                        )
                    )
                }) {
                    Text(text = "Add task")
                }
                Button(onClick = {
                    zerar.value = !zerar.value
                }) {
                    Text(text = "Zerar")
                }
            }
            ChecklistInformationHeaderComponent(
                "Checklist Name",
                10,
                20
            )
        }
    }
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
                AnimatedUncompletedTasksChangeComponent(completedTasksCount = count.intValue)
            }
        }
    }
}



