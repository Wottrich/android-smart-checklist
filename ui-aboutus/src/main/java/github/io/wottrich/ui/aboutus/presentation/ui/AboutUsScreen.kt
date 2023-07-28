package github.io.wottrich.ui.aboutus.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import github.io.wottrich.ui.aboutus.R.string
import github.io.wottrich.ui.aboutus.data.model.AboutUsContentModel
import wottrich.github.io.smartchecklist.baseui.TopBarContent
import wottrich.github.io.smartchecklist.baseui.icons.ArrowBackIcon
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AboutUsScreen(
    model: AboutUsContentModel,
    onBackButton: () -> Unit,
    onVersionAppClick: () -> Unit
) {
    ApplicationTheme {
        Screen(
            model = model,
            onBackButton = onBackButton,
            onVersionAppClick = onVersionAppClick
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun Screen(
    model: AboutUsContentModel,
    onBackButton: () -> Unit,
    onVersionAppClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBarContent(
                title = {
                    Text(stringResource(string.about_us_screen_title))
                },
                navigationIcon = {
                    ArrowBackIcon()
                },
                navigationIconAction = onBackButton
            )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            versionAppItem(
                versionApp = model.getVersionApp(),
                onClick = onVersionAppClick
            )
        }
    }
}

@ExperimentalMaterialApi
private fun LazyListScope.versionAppItem(versionApp: String, onClick: () -> Unit) {
    item {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ListItem(
                modifier = Modifier.clickable { onClick() },
                text = {
                    Text(versionApp)
                },
                secondaryText = {
                    Text(stringResource(id = string.about_us_screen_app_version_label))
                }
            )
            Divider(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}