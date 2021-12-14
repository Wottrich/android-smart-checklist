package wottrich.github.io.publicandroid.presentation.ui

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import wottrich.github.io.publicandroid.presentation.viewmodel.TaskListViewModel

@Composable
fun TaskListScreen(
    checklistId: String,
    viewModel: TaskListViewModel = getViewModel {
        parametersOf(checklistId)
    }
) {
    TaskContentComponent(
        tasks = viewModel.tasks,
        onAddClicked = viewModel::onAddClicked,
        onUpdateClicked = viewModel::onUpdateClicked,
        onDeleteClicked = viewModel::onDeleteClicked
    )
}