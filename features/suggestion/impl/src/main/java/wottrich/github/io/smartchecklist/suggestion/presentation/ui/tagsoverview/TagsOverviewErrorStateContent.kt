package wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagsoverview

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.commonuicompose.components.DefaultErrorScreenContent

@Composable
internal fun TagsOverviewErrorStateContent(onTryAgainClicked: () -> Unit) {
    DefaultErrorScreenContent(onButtonClick = onTryAgainClicked)
}

@Preview
@Composable
fun TagsOverviewErrorStateContentPreview() {
    ApplicationTheme {
        Surface {
            TagsOverviewErrorStateContent { }
        }
    }
}