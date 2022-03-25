package github.io.wottrich.ui.support.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import github.io.wottrich.ui.support.R
import github.io.wottrich.ui.support.presentation.viewmodels.HelpOverviewViewModel
import org.koin.androidx.compose.getViewModel
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Screen(
    onBackPressed: () -> Unit,
    viewModel: HelpOverviewViewModel = getViewModel()
) {

    val state by viewModel.uiState.collectAsState()

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
                items(state.helpItems) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ListItem(
                            text = {
                                Text(text = stringResource(id = it.label))
                            },
                            secondaryText = {
                                Text(text = stringResource(id = it.description))
                            }
                        )
                        Divider(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        )
    }
}