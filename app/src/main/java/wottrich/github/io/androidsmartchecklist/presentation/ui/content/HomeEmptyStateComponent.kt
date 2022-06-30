package wottrich.github.io.androidsmartchecklist.presentation.ui.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun HomeEmptyStateComponent(onNewChecklistClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.BaseFour.SizeTwo)
            .clickable { onNewChecklistClicked() },
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
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistEmptyStatePreview() {
    HomeEmptyStateComponent { }
}