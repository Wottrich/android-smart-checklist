package wottrich.github.io.smartchecklist.presentation.sort.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.presentation.sort.model.TaskSortItemState

@Composable
fun TaskSortContent(
    modifier: Modifier,
    showContent: Boolean,
    sortItems: List<TaskSortItemState>,
    onSortItemClicked: (TaskSortItemState) -> Unit
) {
    AnimatedVisibility(
        visible = showContent,
        enter = fadeIn() + expandVertically(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            TaskSortComponent(sortItems = sortItems, onSortItemClicked = onSortItemClicked)
            Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeThree))
        }
    }
}

@Composable
private fun TaskSortComponent(
    sortItems: List<TaskSortItemState>,
    onSortItemClicked: (TaskSortItemState) -> Unit
) {
    LazyRow(
        content = {
            items(sortItems) {
                TaskSortItem(
                    state = it,
                    onClick = onSortItemClicked
                )
                Spacer(modifier = Modifier.width(Dimens.BaseFour.SizeOne))
            }
        }
    )
}