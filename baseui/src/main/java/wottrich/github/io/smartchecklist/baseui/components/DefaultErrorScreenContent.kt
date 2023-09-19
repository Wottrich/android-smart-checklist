package wottrich.github.io.smartchecklist.baseui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import wottrich.github.io.smartchecklist.baseui.R
import wottrich.github.io.smartchecklist.baseui.previewparams.BooleanPreviewParameter
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.CenterTextContent
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.RowDefaults

@Composable
fun DefaultErrorScreenContent(
    title: String = stringResource(id = R.string.attention),
    description: String = stringResource(id = R.string.unknown),
    buttonLabel: String = stringResource(id = R.string.try_again),
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.BaseFour.SizeTwo),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(Dimens.BaseFour.SizeTen),
            imageVector = Icons.Default.Info,
            contentDescription = null
        )
        CenterTextContent(
            primary = RowDefaults.title(text = title, fontWeight = FontWeight.Bold),
            secondary = RowDefaults.description(text = description)
        )
        SmartChecklistButton(onClick = onButtonClick) {
            Text(text = buttonLabel)
        }
    }
}

@Preview
@Composable
fun DefaultErrorScreenContentPreview(
    @PreviewParameter(BooleanPreviewParameter::class) isDarkMode: Boolean
) {
    ApplicationTheme(isDarkMode) {
        Surface {
            DefaultErrorScreenContent { }
        }
    }
}