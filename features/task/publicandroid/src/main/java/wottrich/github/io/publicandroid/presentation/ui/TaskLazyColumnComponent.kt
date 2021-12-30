package wottrich.github.io.publicandroid.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import wottrich.github.io.baseui.TextOneLine
import wottrich.github.io.baseui.ui.Dimens
import wottrich.github.io.database.entity.Task
import wottrich.github.io.publicandroid.R

private const val ANIMATION_DURATION = 200
private val EnterAnimation = slideInHorizontally(animationSpec = tween(ANIMATION_DURATION))
private val ExitAnimation = shrinkVertically(animationSpec = tween(ANIMATION_DURATION))

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun TaskLazyColumnComponent(
    taskList: List<Task>,
    onCheckChange: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    showDeleteItem: Boolean = true
) {
    val hasCompletedList = taskList.any { it.isCompleted }
    LazyColumn(
        content = {
            items(taskList) { task ->
                AnimatedVisibility(
                    visible = !task.isCompleted,
                    enter = EnterAnimation,
                    exit = ExitAnimation
                ) {
                    TaskItem(
                        task = task,
                        showDeleteItem = showDeleteItem,
                        onCheckChange = onCheckChange,
                        onDeleteTask = onDeleteTask
                    )
                }
            }
            if (hasCompletedList) {
                completedHeader()
            }
            items(taskList) { task ->
                AnimatedVisibility(
                    visible = task.isCompleted,
                    enter = EnterAnimation,
                    exit = ExitAnimation
                ) {
                    TaskItem(
                        task = task,
                        showDeleteItem = showDeleteItem,
                        onCheckChange = onCheckChange,
                        onDeleteTask = onDeleteTask
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.completedHeader() {
    stickyHeader {
        Box(modifier = Modifier.padding(vertical = Dimens.BaseFour.SizeThree)) {
            Divider()
            Box(
                modifier = Modifier
                    .padding(horizontal = Dimens.BaseFour.SizeThree)
                    .padding(top = Dimens.BaseFour.SizeThree)
            ) {
                TextOneLine(
                    primary = {
                        Text(stringResource(id = R.string.task_list_completed_tasks_header))
                    }
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    showDeleteItem: Boolean,
    onCheckChange: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
        TaskItemComponent(
            task = task,
            showDeleteItem = showDeleteItem,
            onCheckChange = { onCheckChange(task) },
            onDeleteTask = { onDeleteTask(task) }
        )
    }
}