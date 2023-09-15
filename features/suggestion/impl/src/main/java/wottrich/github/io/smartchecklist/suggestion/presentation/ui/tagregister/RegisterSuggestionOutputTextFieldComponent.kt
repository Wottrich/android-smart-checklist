package wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagregister

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.color.defaultOutlinedTextFieldColors
import wottrich.github.io.smartchecklist.suggestion.R

@Composable
fun RegisterSuggestionOutputTextFieldComponent(
    suggestionName: String,
    onTextFieldValueChange: (String) -> Unit,
    onAddButtonClicked: () -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = suggestionName,
            onValueChange = onTextFieldValueChange,
            trailingIcon = {
                IconButton(onClick = onAddButtonClicked) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            },
            label = {
                LabelContent()
            },
            placeholder = {
                PlaceholderContent()
            },
            colors = defaultOutlinedTextFieldColors()
        )
        Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
    }
}

@Composable
private fun LabelContent() {
    Text(text = stringResource(id = R.string.tags_register_screen_suggestion_text_field_label))
}

@Composable
private fun PlaceholderContent() {
    Text(text = stringResource(id = R.string.tags_register_screen_suggestion_text_field_placeholder))
}