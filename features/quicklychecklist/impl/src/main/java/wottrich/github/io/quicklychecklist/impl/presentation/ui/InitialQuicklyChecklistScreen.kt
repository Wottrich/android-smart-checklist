package wottrich.github.io.quicklychecklist.impl.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.baseui.ui.ApplicationTheme
import wottrich.github.io.baseui.ui.Dimens.BaseFour
import wottrich.github.io.baseui.ui.color.defaultButtonColors
import wottrich.github.io.baseui.ui.color.defaultOutlinedTextFieldColors
import wottrich.github.io.quicklychecklist.impl.R
import wottrich.github.io.quicklychecklist.impl.presentation.viewmodels.InitialQuicklyChecklistViewModel

@Composable
fun InitialQuicklyChecklistScreen(
    onBackPressed: () -> Unit
) {
    ApplicationTheme {
        Screen(onBackPressed = onBackPressed)
    }
}

@Composable
private fun Screen(
    onBackPressed: () -> Unit,
    viewModel: InitialQuicklyChecklistViewModel = getViewModel()
) {
    Scaffold(
        topBar = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.arrow_back_content_description),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues)
                .padding(all = BaseFour.SizeThree)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                QuicklyChecklistField(
                    textFieldValue = viewModel.quicklyChecklistJson,
                    onTextFieldValueChange = {
                        viewModel.onQuicklyChecklistJsonChange(it)
                    }
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = viewModel.isButtonEnabled,
                onClick = viewModel::onConfirmButtonClicked,
                colors = defaultButtonColors()
            ) {
                Text(text = stringResource(id = R.string.confirm))
            }
        }
    }
}

@Composable
private fun QuicklyChecklistField(
    textFieldValue: String,
    onTextFieldValueChange: (String) -> Unit
) {
    TextField(
        value = textFieldValue,
        onValueChange = onTextFieldValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(text = stringResource(id = R.string.quickly_checklist_screen_field_hint))
        },
        colors = defaultOutlinedTextFieldColors()
    )
}

@Preview(showSystemUi = true)
@Composable
fun QuicklyChecklistScreenPreview() {
    ApplicationTheme {
        InitialQuicklyChecklistScreen {}
    }
}