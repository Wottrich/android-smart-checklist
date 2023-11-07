package wottrich.github.io.smartchecklist.presentation.ui.checklistinformationheader

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.RowDefaults
import wottrich.github.io.smartchecklist.baseui.ui.TextStateComponent
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme
import wottrich.github.io.smartchecklist.datasource.entity.NewTask

@Composable
fun ChecklistInformationHeaderComponent(
    onTaskCounterClicked: () -> Unit,
    viewModel: ChecklistInformationHeaderViewModel = getViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    ChecklistInformationHeader(state, onTaskCounterClicked)
}

@Composable
private fun ChecklistInformationHeader(
    state: ChecklistInformationHeaderUiState,
    onTaskCounterClicked: () -> Unit
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
                    state.checklistName,
                    style = MaterialTheme.typography.h5
                )
            )
            CompletableCountComponent(
                state.completedTasksCount,
                onTaskCounterClicked
            )
        }
        CompletableProgressComponent(
            itemWidth.intValue.toFloat(),
            state.completedTasksCount,
            state.totalTasksCount
        )
    }
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
            ChecklistInformationHeader(
                ChecklistInformationHeaderUiState(
                    isLoading = false,
                    checklistName = "Checklist name",
                    completedTasksCount = 10,
                    totalTasksCount = 20
                )
            ) {}
        }
    }
}



