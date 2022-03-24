package github.io.wottrich.ui.support.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import github.io.wottrich.ui.support.R
import wottrich.github.io.baseui.TextOneLine
import wottrich.github.io.baseui.TopBarContent
import wottrich.github.io.baseui.icons.ArrowBackIcon
import wottrich.github.io.baseui.ui.ApplicationTheme
import wottrich.github.io.baseui.ui.Dimens.BaseFour

@Composable
fun HelpOverviewScreen(onBackPressed: () -> Unit) {
    ApplicationTheme {
        Screen(
            onBackPressed = onBackPressed
        )
    }
}

@Composable
private fun Screen(onBackPressed: () -> Unit) {
    Scaffold(
        topBar = {
            TopBarContent(
                title = {
                    Text(stringResource(R.string.support_help_overview_title))
                },
                navigationIcon = {
                    ArrowBackIcon()
                },
                navigationIconAction = onBackPressed
            )
        }
    ) {
        LazyColumn(
            content = {
                item {
                    TextOneLine(
                        modifier = Modifier.padding(all = BaseFour.SizeThree),
                        primary = {
                            Text(
                                text = stringResource(
                                    id = R.string.support_help_overview_tutorial_label
                                )
                            )
                        }
                    )
                }
            }
        )
    }
}