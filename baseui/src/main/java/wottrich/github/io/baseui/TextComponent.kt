package wottrich.github.io.baseui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import wottrich.github.io.baseui.ui.SmartChecklistTheme

@Composable
fun SingleRow(
    modifier: Modifier = Modifier,
    primary: @Composable () -> Unit,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    Column(modifier = modifier, horizontalAlignment = horizontalAlignment) {
        StyledText(textStyle = MaterialTheme.typography.h6, content = primary)
    }
}

@Composable
fun TwoLine(
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
    alpha: Float = 1f,// LocalContentAlpha.current,
    contentColor: Color = SmartChecklistTheme.textColors.primary,
    content: @Composable (() -> Unit)
) {
    CompositionLocalProvider(
        LocalContentAlpha provides alpha,
        LocalContentColor provides contentColor,
    ) {
        ProvideTextStyle(textStyle, content)
    }
}