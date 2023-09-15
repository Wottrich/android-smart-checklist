package wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagregister

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import wottrich.github.io.smartchecklist.baseui.ui.color.defaultOutlinedTextFieldColors
import wottrich.github.io.smartchecklist.suggestion.R

@Composable
internal fun EditTagNameComponent(
    modifier: Modifier = Modifier,
    tagName: String,
    onTextFieldValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = tagName,
        onValueChange = onTextFieldValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(text = stringResource(id = R.string.tags_register_screen_tag_text_field_placeholder_label))
        },
        colors = defaultOutlinedTextFieldColors()
    )
}