package wottrich.github.io.smartchecklist.uiprivacypolicy.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import wottrich.github.io.smartchecklist.baseui.StyledText
import wottrich.github.io.smartchecklist.baseui.TopBarContent
import wottrich.github.io.smartchecklist.baseui.components.SmartChecklistButton
import wottrich.github.io.smartchecklist.baseui.icons.ArrowBackIcon
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.uiprivacypolicy.R

@Composable
fun PrivacyPolicyScreen(onOpenPrivacyPolicy: () -> Unit, onBackButton: () -> Unit) {
    ApplicationTheme {
        PrivacyPolicyComponent(onOpenPrivacyPolicy, onBackButton)
    }
}

@Composable
private fun PrivacyPolicyComponent(onOpenPrivacyPolicy: () -> Unit, onBackButton: () -> Unit) {
    Scaffold(
        topBar = {
            TopBarContent(
                title = {},
                navigationIcon = {
                    ArrowBackIcon()
                },
                navigationIconAction = onBackButton
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(Dimens.BaseFour.SizeFour),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                StyledText(textStyle = MaterialTheme.typography.h4) {
                    Text(text = stringResource(id = R.string.privacy_policy_title))
                }
                StyledText(textStyle = MaterialTheme.typography.body1) {
                    Text(text = stringResource(id = R.string.privacy_policy_description))
                }
            }
            SmartChecklistButton(
                onClick = onOpenPrivacyPolicy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.privacy_policy_button))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrivacyPolicyComponentPreview() {
    ApplicationTheme {
        PrivacyPolicyComponent(
            onOpenPrivacyPolicy = {},
            onBackButton = {}
        )
    }
}