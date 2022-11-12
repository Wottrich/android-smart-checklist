package wottrich.github.io.quicklychecklist.impl.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import wottrich.github.io.baseui.ui.Dimens.BaseFour
import wottrich.github.io.baseui.ui.color.defaultButtonColors
import wottrich.github.io.baseui.ui.color.defaultOutlinedTextFieldColors
import wottrich.github.io.datasource.entity.QuicklyChecklist
import wottrich.github.io.quicklychecklist.impl.R
import wottrich.github.io.quicklychecklist.impl.presentation.viewmodels.QuicklyChecklistAddNewChecklistViewModel

@Composable
fun ColumnScope.QuicklyChecklistAddNewChecklistBottomSheetContent(
    quicklyChecklist: QuicklyChecklist?,
    viewModel: QuicklyChecklistAddNewChecklistViewModel = getViewModel {
        parametersOf(quicklyChecklist)
    }
) {
    val state by viewModel.uiState.collectAsState()
    Column {
        TextField(
            value = state.checklistName,
            onValueChange = viewModel::onTextChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = stringResource(id = R.string.quickly_checklist_type_checklist_name_hint))
            },
            colors = defaultOutlinedTextFieldColors()
        )
    }
    Spacer(modifier = Modifier.height(BaseFour.SizeTwo))
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = state.isButtonEnabled,
        onClick = viewModel::onConfirmButtonClicked,
        colors = defaultButtonColors()
    ) {
        Text(text = stringResource(id = R.string.quickly_checklist_confirm_new_checklist_name))
    }
}