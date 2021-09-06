package wottrich.github.io.featurenew.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.components.TitleRow
import wottrich.github.io.components.ui.Sizes
import wottrich.github.io.components.ui.defaultButtonColors
import wottrich.github.io.components.ui.defaultOutlinedTextFieldColors
import wottrich.github.io.featurenew.R
import wottrich.github.io.featurenew.view.ChecklistNameViewModel
import wottrich.github.io.featurenew.view.NewChecklistFlow

@Composable
fun ChecklistNameScreen(
    navHostController: NavHostController,
    viewModel: ChecklistNameViewModel = getViewModel()
) {

    val state by viewModel.screenState.collectAsState()

    when {
        state.isInitialState -> Screen(viewModel = viewModel)
        state.isNextScreen -> Unit //TODO implement next screen action
        state.isError -> Unit //TODO implement error state
    }

}

@Composable
private fun Screen(viewModel: ChecklistNameViewModel) {

    var textFieldValue by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(all = Sizes.x12)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            TitleRow(text = stringResource(id = R.string.new_checklist_type_checklist_name_hint))

            OutlinedTextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = stringResource(id = R.string.new_checklist_example_hint))
                },
                colors = defaultOutlinedTextFieldColors()
            )
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = textFieldValue.isNotEmpty(),
            onClick = {

            },
            colors = defaultButtonColors()
        ) {
            Text(text = stringResource(id = R.string.new_checklist_button_continue))
        }
    }
}