package wottrich.github.io.smartchecklist.presentation.ui.drawer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import wottrich.github.io.smartchecklist.R
import wottrich.github.io.smartchecklist.baseui.components.SmartChecklistButton

@Composable
fun AddChecklistButtonContent(onAddNewChecklist: () -> Unit) {
    val buttonContentDescription = stringResource(id = R.string.floating_action_content_description)
    SmartChecklistButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onAddNewChecklist,
        buttonContentDescription = buttonContentDescription
    ) {
        Text(
            text = stringResource(
                id = wottrich.github.io.smartchecklist.checklist.R.string.new_checklist_activity_screen_title
            ).uppercase()
        )
    }
}