package wottrich.github.io.smartchecklist.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import wottrich.github.io.smartchecklist.baseui.TextOneLine
import wottrich.github.io.smartchecklist.baseui.components.SmartChecklistButton
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.color.defaultOutlinedTextFieldColors
import wottrich.github.io.smartchecklist.task.R

@Composable
fun TaskEditHeaderComponent(
    textFieldValue: String,
    showSectionButton: Boolean,
    onTextFieldValueChange: ((String) -> Unit),
    onAddItem: (() -> Unit),
    onAddSection: (() -> Unit)
) {
    Column(modifier = Modifier.padding(horizontal = Dimens.BaseFour.SizeThree)) {
        TextOneLine(
            modifier = Modifier.padding(vertical = Dimens.BaseFour.SizeTwo),
            primary = {
                Text(text = stringResource(id = R.string.task_header_list_title))
            }
        )
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = onTextFieldValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = stringResource(id = R.string.task_header_type_hint))
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onAddItem() }),
            colors = defaultOutlinedTextFieldColors()
        )
        Row {
            if (showSectionButton) {
                SmartChecklistButton(
                    modifier = Modifier
                        .padding(top = Dimens.BaseFour.SizeTwo)
                        .fillMaxWidth()
                        .weight(1f),
                    enabled = textFieldValue.isNotEmpty(),
                    onClick = onAddSection,
                ) {
                    Text(text = stringResource(id = R.string.task_header_add_section))
                }
            }
            SmartChecklistButton(
                modifier = Modifier
                    .padding(top = Dimens.BaseFour.SizeTwo)
                    .fillMaxWidth()
                    .weight(1f),
                enabled = textFieldValue.isNotEmpty(),
                onClick = onAddItem,
            ) {
                Text(text = stringResource(id = R.string.task_header_add_item))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskEditHeaderComponentPreview() {
    ApplicationTheme {
        TaskEditHeaderComponent(
            "",
            showSectionButton = false,
            {},
            {},
            {}
        )
    }
}