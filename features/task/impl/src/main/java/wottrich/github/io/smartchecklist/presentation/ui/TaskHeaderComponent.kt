package wottrich.github.io.smartchecklist.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import wottrich.github.io.smartchecklist.baseui.TextOneLine
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.color.defaultButtonColors
import wottrich.github.io.smartchecklist.baseui.ui.color.defaultOutlinedTextFieldColors
import wottrich.github.io.smartchecklist.task.R

@Composable
fun TaskHeaderComponent(
    textFieldValue: String,
    onTextFieldValueChange: ((String) -> Unit),
    onAddItem: (() -> Unit)
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
                Text(text = stringResource(id = R.string.task_header_type_task_name_hint))
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onAddItem() }),
            colors = defaultOutlinedTextFieldColors()
        )
        Button(
            modifier = Modifier
                .padding(top = Dimens.BaseFour.SizeTwo)
                .fillMaxWidth(),
            enabled = textFieldValue.isNotEmpty(),
            onClick = onAddItem,
            colors = defaultButtonColors(),
        ) {
            Text(text = stringResource(id = R.string.task_header_add_item))
        }
    }
}