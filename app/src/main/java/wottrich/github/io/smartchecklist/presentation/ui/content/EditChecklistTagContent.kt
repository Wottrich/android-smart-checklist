package wottrich.github.io.smartchecklist.presentation.ui.content

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.smartchecklist.R
import wottrich.github.io.smartchecklist.baseui.StyledText
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme
import wottrich.github.io.smartchecklist.presentation.viewmodel.EditChecklistTagContentAction
import wottrich.github.io.smartchecklist.presentation.viewmodel.EditChecklistTagContentViewModel

private val Modifier.editChecklistTagModifier: Modifier
    get() = composed {
        Modifier
            .padding(horizontal = Dimens.BaseFour.SizeThree)
            .clip(CircleShape)
            .border(
                border = BorderStroke(1.dp, color = SmartChecklistTheme.colors.onSurface),
                shape = CircleShape
            )
    }

@Composable
fun ColumnScope.EditChecklistTagContent(
    isEditMode: Boolean,
    onEditTagClicked: () -> Unit,
    viewModel: EditChecklistTagContentViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsState()
    AnimatedVisibility(
        visible = isEditMode,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        LaunchedEffect(key1 = isEditMode) {
            if (isEditMode) {
                viewModel.sendAction(EditChecklistTagContentAction.Action.OnStartEditLabelAnimation)
            } else {
                viewModel.sendAction(EditChecklistTagContentAction.Action.OnStopEditLabelAnimation)
            }
        }
        EditChecklistTagComponent(
            isEditLabelVisible = state.showEditLabel,
            onEditTagClicked = onEditTagClicked
        )
    }
}

@Composable
private fun EditChecklistTagComponent(
    isEditLabelVisible: Boolean,
    onEditTagClicked: () -> Unit
) {
    val contentDescription = stringResource(
        id = R.string.checklist_edit_tags_content_description
    )
    Row(
        modifier = Modifier.editChecklistTagModifier
            .clickable { onEditTagClicked() }
            .padding(vertical = Dimens.BaseFour.SizeTwo, horizontal = Dimens.BaseFour.SizeThree)
            .semantics { this.contentDescription = contentDescription },
        verticalAlignment = Alignment.CenterVertically
    ) {
        TagEditLabelComponent(isEditLabelVisible = isEditLabelVisible)
        TagIconComponent()
    }
}

@Composable
private fun TagEditLabelComponent(
    isEditLabelVisible: Boolean
) {
    AnimatedVisibility(
        visible = isEditLabelVisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkHorizontally()
    ) {
        Row {
            StyledText(textStyle = MaterialTheme.typography.subtitle2) {
                Text(text = "Edit tags", color = SmartChecklistTheme.colors.onSurface)
            }
            Spacer(modifier = Modifier.width(Dimens.BaseFour.SizeOne))
        }
    }
}

@Composable
private fun TagIconComponent() {
    Icon(
        tint = SmartChecklistTheme.colors.onSurface,
        painter = painterResource(id = R.drawable.ic_edit_tags),
        contentDescription = null
    )
}

abstract class TabItemModel {
    abstract val label: String
    protected abstract val icon: Int
    abstract val isSelected: Boolean
    open val badgeNumber: Int? = null

    @Composable
    open fun getPainterIcon(): Painter {
        return painterResource(id = icon)
    }
}

data class HomeTabItemModel(
    override val label: String,
    override val isSelected: Boolean,
    override val badgeNumber: Int?
) : TabItemModel() {

    override val icon: Int
        get() = if (isSelected) wottrich.github.io.smartchecklist.suggestion.R.drawable.check_box_checked
                else wottrich.github.io.smartchecklist.suggestion.R.drawable.check_box_unchecked

}

fun foo() {
    HomeTabItemModel(
        "Home",
        false,
        badgeNumber = 3
    )
}