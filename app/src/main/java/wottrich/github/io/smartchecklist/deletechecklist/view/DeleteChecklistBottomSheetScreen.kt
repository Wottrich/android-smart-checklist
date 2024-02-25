package wottrich.github.io.smartchecklist.deletechecklist.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel
import wottrich.github.io.smartchecklist.R
import wottrich.github.io.smartchecklist.baseui.StyledText
import wottrich.github.io.smartchecklist.baseui.components.ButtonVariant
import wottrich.github.io.smartchecklist.baseui.components.SmartChecklistButton
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.deletechecklist.viewmodel.DeleteChecklistBottomSheetViewModel
import wottrich.github.io.smartchecklist.deletechecklist.viewmodel.DeleteChecklistUiEffect
import wottrich.github.io.smartchecklist.baseui.R as BaseUiR

@Composable
fun DeleteChecklistBottomSheetScreen(
    onCloseFlow: () -> Unit,
    onCloseBottomSheet: () -> Unit,
    viewModel: DeleteChecklistBottomSheetViewModel = koinViewModel()
) {
    Effects(
        viewModel = viewModel,
        onDeletedWithSuccess = onCloseFlow
    )
    DeleteChecklistBottomSheetComponent(
        onConfirm = { viewModel.onDeleteChecklistClicked() },
        onCancel = { onCloseBottomSheet() }
    )
}

@Composable
private fun Effects(
    viewModel: DeleteChecklistBottomSheetViewModel,
    onDeletedWithSuccess: () -> Unit
) {
    val effects = viewModel.uiEffect
    LaunchedEffect(key1 = effects) {
        effects.collect { effect ->
            when (effect) {
                DeleteChecklistUiEffect.DeletedWithSuccess -> onDeletedWithSuccess()
            }
        }
    }
}

@Composable
private fun DeleteChecklistBottomSheetComponent(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    ApplicationTheme {
        Surface {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StyledText(textStyle = MaterialTheme.typography.h5) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = Dimens.BaseFour.SizeTwo)
                            .padding(top = Dimens.BaseFour.SizeTwo),
                        text = stringResource(id = BaseUiR.string.attention)
                    )
                }
                Text(
                    modifier = Modifier
                        .padding(horizontal = Dimens.BaseFour.SizeTwo)
                        .padding(top = Dimens.BaseFour.SizeTwo),
                    text = stringResource(id = R.string.checklist_delete_checklist_confirm_label),
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SmartChecklistButton(
                        modifier = Modifier.weight(1f),
                        buttonVariant = ButtonVariant.NEGATIVE,
                        onClick = onConfirm
                    ) {
                        Text(text = stringResource(id = BaseUiR.string.yes))
                    }
                    SmartChecklistButton(
                        modifier = Modifier.weight(1f),
                        onClick = onCancel
                    ) {
                        Text(text = stringResource(id = BaseUiR.string.no))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteChecklistBottomSheetComponentPreview() {
    DeleteChecklistBottomSheetComponent(
        onConfirm = { },
        onCancel = { }
    )
}