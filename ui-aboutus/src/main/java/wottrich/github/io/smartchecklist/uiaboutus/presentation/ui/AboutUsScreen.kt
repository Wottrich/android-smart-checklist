package wottrich.github.io.smartchecklist.uiaboutus.presentation.ui

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
import wottrich.github.io.smartchecklist.uiaboutus.data.model.AboutUsContentModel
import wottrich.github.io.smartchecklist.baseui.TopBarContent
import wottrich.github.io.smartchecklist.baseui.icons.ArrowBackIcon
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.uiaboutus.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AboutUsScreen(
    model: AboutUsContentModel,
    onBackButton: () -> Unit,
    onVersionAppClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
) {
    ApplicationTheme {
        Screen(
            model = model,
            onBackButton = onBackButton,
            onVersionAppClick = onVersionAppClick,
            onPrivacyPolicyClick = onPrivacyPolicyClick
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun Screen(
    model: AboutUsContentModel,
    onBackButton: () -> Unit,
    onVersionAppClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBarContent(
                title = {
                    Text(stringResource(R.string.about_us_screen_title))
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
            privacyPolicyItem(onPrivacyPolicyClick)
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
                    Text(stringResource(id = R.string.about_us_screen_app_version_label))
                }
            )
            Divider(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
private fun LazyListScope.privacyPolicyItem(onClick: () -> Unit) {
    item {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ListItem(
                modifier = Modifier.clickable { onClick() },
                text = {
                    Text(stringResource(id = R.string.about_us_screen_privacy_policy_label))
                }
            )
            Divider(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}