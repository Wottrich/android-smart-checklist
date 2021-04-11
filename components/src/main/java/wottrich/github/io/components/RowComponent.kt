package wottrich.github.io.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import wottrich.github.io.tools.dimens.Dimens

@Composable
fun RowComponent(
    modifier: Modifier = Modifier,
    leftIconContent: @Composable (RowScope.() -> Unit)? = null,
    rightIconContent: @Composable (RowScope.() -> Unit)? = null,
    rightContent: @Composable (ColumnScope.() -> Unit)? = null,
    leftContent: @Composable ColumnScope.() -> Unit,
) {

    val boxModifier = modifier
        .fillMaxWidth()

    Box(
        modifier = boxModifier
    ) {
        val rowModifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.BaseFour.SizeThree)
        Row(
            modifier = rowModifier
        ) {
            LeftContent(
                iconContent = leftIconContent,
                content = leftContent
            )
            RightContent(
                iconContent = rightIconContent,
                content = rightContent
            )
        }
    }
}

@Composable
private fun RowScope.LeftContent(
    iconContent: @Composable (RowScope.() -> Unit)? = null,
    content: @Composable (ColumnScope.() -> Unit)? = null
) {
    val rowModifier = Modifier
        .fillMaxWidth()
        .weight(1f)
    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        iconContent?.invoke(this)
        content?.let {
            Spacer(modifier = Modifier.width(Dimens.BaseFour.SizeTwo))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                it()
            }
        }
    }
}

@Composable
private fun RowScope.RightContent(
    iconContent: @Composable (RowScope.() -> Unit)? = null,
    content: @Composable (ColumnScope.() -> Unit)? = null
) {
    val rowModifier = Modifier
        .fillMaxWidth()
        .weight(1f)
    Row(
        modifier = rowModifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content?.let {
            Column(
                modifier = Modifier.wrapContentSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                it()
            }
            Spacer(modifier = Modifier.width(Dimens.BaseFour.SizeTwo))
        }
        iconContent?.invoke(this)
    }
}

@Preview(showBackground = true)
@Composable
fun RowComponentPreview() {
    RowComponent(
        leftContent = {
            Text(text = "Text")
        }
    )
}