package wottrich.github.io.smartchecklist.baseui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

@Composable
fun TextOneLine(
    modifier: Modifier = Modifier,
    primary: @Composable () -> Unit,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    Column(modifier = modifier, horizontalAlignment = horizontalAlignment) {
        StyledText(textStyle = MaterialTheme.typography.h6, content = primary)
    }
}

@Composable
fun TextTwoLine(
    modifier: Modifier = Modifier,
    primary: @Composable () -> Unit,
    secondary: @Composable () -> Unit,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    Column(modifier = modifier, horizontalAlignment = horizontalAlignment) {
        StyledText(textStyle = MaterialTheme.typography.h6, content = primary)
        StyledText(
            textStyle = MaterialTheme.typography.subtitle1,
            alpha = ContentAlpha.medium,
            content = secondary
        )
    }
}

@Composable
fun StyledText(
    textStyle: TextStyle,
    alpha: Float = LocalContentAlpha.current,
    contentColor: Color = SmartChecklistTheme.colors.onPrimary,
    content: @Composable (() -> Unit)
) {
    CompositionLocalProvider(
        LocalContentAlpha provides alpha,
        LocalContentColor provides contentColor,
    ) {
        ProvideTextStyle(textStyle, content)
    }
}

@Preview
@Composable
private fun TextComponentPreview() {
    ApplicationTheme {
        Scaffold() {
            Column(
                modifier = Modifier.padding(it)
            ) {
                TextOneLine(
                    primary = {
                        Text("Hellow")
                    }
                )
            }
        }
    }
}