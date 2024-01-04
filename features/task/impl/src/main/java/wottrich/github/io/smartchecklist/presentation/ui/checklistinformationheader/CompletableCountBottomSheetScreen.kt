package wottrich.github.io.smartchecklist.presentation.ui.checklistinformationheader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.smartchecklist.baseui.components.SmartChecklistButton
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.RowDefaults
import wottrich.github.io.smartchecklist.baseui.ui.TextStateComponent
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme
import wottrich.github.io.smartchecklist.task.R

@Composable
fun CompletableCountBottomSheetScreen(
    onClose: () -> Unit,
    viewModel: CompletableCountBottomSheetViewModel = getViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    ApplicationTheme {
        CompletableCounterComponent(
            state = state,
            onClose = onClose
        )
    }
}

@Composable
private fun CompletableCounterComponent(
    state: CompletableCountUiState,
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(
            topStart = Dimens.BaseFour.SizeThree,
            topEnd = Dimens.BaseFour.SizeThree
        ),
        color = SmartChecklistTheme.colors.background
    ) {
        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.BaseFour.SizeFour)
            ) {
                CircularProgressIndicator()
            }
        } else {
            CompletableCounterContent(
                state = state,
                onClose = onClose
            )
        }
    }
}

@Composable
private fun CompletableCounterContent(
    state: CompletableCountUiState,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = Dimens.BaseFour.SizeThree,
                    topEnd = Dimens.BaseFour.SizeThree
                )
            )
            .padding(
                horizontal = Dimens.BaseFour.SizeFour,
            )
            .padding(
                top = Dimens.BaseFour.SizeFour,
                bottom = Dimens.BaseFour.SizeOne
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextStateComponent(
            textState = RowDefaults.title(
                text = stringResource(id = R.string.completable_count_bottom_sheet_title),
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeThree))
        TextStateComponent(
            textState = RowDefaults.description(
                text = stringResource(
                    id = R.string.completable_count_bottom_sheet_subtitle,
                    state.completedTasksCount.toString(),
                    state.totalTasksCount.toString()
                )
            )
        )
        Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
        CompletableCountComponent(completedTasks = state.completedTasksCount)
        TextStateComponent(
            textState = RowDefaults.smallDescription(
                text = stringResource(id = R.string.completable_count_completable_component_tip),
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeThree))
        SmartChecklistButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClose
        ) {
            Text(text = stringResource(id = R.string.completable_count_bottom_sheet_button_label))
        }
    }
}

@Preview
@Composable
private fun CompletableCountBottomSheetScreenPreview() {
    CompletableCounterComponent(
        CompletableCountUiState(
            isLoading = false,
            completedTasksCount = 10,
            totalTasksCount = 20
        )
    ) {}
}