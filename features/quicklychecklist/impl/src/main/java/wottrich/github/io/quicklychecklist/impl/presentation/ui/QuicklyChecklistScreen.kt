package wottrich.github.io.quicklychecklist.impl.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import wottrich.github.io.baseui.TopBarContent
import wottrich.github.io.baseui.icons.ArrowBackIcon
import wottrich.github.io.baseui.ui.ApplicationTheme
import wottrich.github.io.impl.presentation.ui.TaskLazyColumnComponent
import wottrich.github.io.quicklychecklist.impl.presentation.viewmodels.QuicklyChecklistViewModel

@Composable
fun QuicklyChecklistScreen(
    quicklyChecklistJson: String,
    onBackPressed: () -> Unit
) {
    ApplicationTheme {
        QuicklyChecklistScaffold(
            quicklyChecklistJson = quicklyChecklistJson,
            onBackPressed = onBackPressed
        )
    }
}

@Composable
private fun QuicklyChecklistScaffold(
    quicklyChecklistJson: String,
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBarContent(
                title = { Text(text = "Telinha bonita") },
                navigationIcon = { ArrowBackIcon() },
                navigationIconAction = onBackPressed
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            Screen(quicklyChecklistJson)
        }
    }
}

@Composable
private fun Screen(
    quicklyChecklistJson: String,
    viewModel: QuicklyChecklistViewModel = getViewModel {
        parametersOf(quicklyChecklistJson)
    }
) {
    TaskLazyColumnComponent(
        taskList = listOf(),
        onCheckChange = {},
        onDeleteTask = {},
        showDeleteItem = false
    )
    Text(text = quicklyChecklistJson)
}