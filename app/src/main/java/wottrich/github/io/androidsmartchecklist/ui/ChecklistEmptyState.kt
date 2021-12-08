package wottrich.github.io.androidsmartchecklist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import wottrich.github.io.androidsmartchecklist.R
import wottrich.github.io.baseui.TextOneLine
import wottrich.github.io.baseui.ui.Dimens
import wottrich.github.io.baseui.ui.RowDefaults
import wottrich.github.io.baseui.ui.TextStateComponent
import wottrich.github.io.baseui.ui.color.defaultButtonColors

@Composable
fun ChecklistEmptyState(onNewChecklistClicked: () -> Unit) {

    Surface(
        modifier = Modifier.padding(
            top = Dimens.BaseFour.SizeTwo,
            start = Dimens.BaseFour.SizeTwo,
            end = Dimens.BaseFour.SizeTwo
        ),
        shape = RoundedCornerShape(Dimens.BaseFour.SizeTwo),
        elevation = Dimens.BaseFour.SizeOne
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.BaseFour.SizeTwo),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextOneLine(
                modifier = Modifier.fillMaxWidth(),
                primary = {
                    TextStateComponent(
                        modifier = Modifier.fillMaxWidth(),
                        textState = RowDefaults.title(
                            text = stringResource(id = R.string.checklist_empty_state),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))

            Button(
                onClick = onNewChecklistClicked,
                colors = defaultButtonColors()
            ) {
                Text(text = stringResource(id = R.string.checklist_empty_state_new_checklist_button))
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ChecklistEmptyStatePreview() {
    ChecklistEmptyState { }
}