package wottrich.github.io.smartchecklist.presentation.ui.sort

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import wottrich.github.io.smartchecklist.baseui.StyledText
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.domain.model.SortItemType
import wottrich.github.io.smartchecklist.task.R

@Composable
fun SortTaskListBottomSheet(
    navController: NavController,
    viewModel: SortTaskListViewModel = koinViewModel()
) {
    val sortItems = viewModel.sortItems
    val snackbarHostState = remember { SnackbarHostState() }
    Effects(
        viewModel = viewModel,
        snackbarHostState = snackbarHostState,
        onCloseBottomSheet = {
            navController.popBackStack()
        }
    )
    ApplicationTheme {
        SortTaskListComponent(
            sortItems = sortItems,
            onSelectedItem = {
                viewModel.sendAction(SortTaskListAction.Action.OnSelectedItem(it))
            }
        )
        SnackbarHost(snackbarHostState)
    }
}

@Composable
private fun Effects(
    viewModel: SortTaskListViewModel,
    snackbarHostState: SnackbarHostState,
    onCloseBottomSheet: () -> Unit
) {
    val effects = viewModel.uiEffect
    val errorMessage = stringResource(id = R.string.task_sort_bottom_sheet_select_sort_failure)
    LaunchedEffect(key1 = effects) {
        effects.collect {
            when (it) {
                SortTaskListUiEffect.CloseBottomSheet -> onCloseBottomSheet()
                SortTaskListUiEffect.SetSortFailureSnackbar -> {
                    snackbarHostState.showSnackbar(errorMessage)
                }
            }
        }
    }
}

@Composable
private fun SortTaskListComponent(
    sortItems: List<SortItem>,
    onSelectedItem: (SortItem) -> Unit
) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.BaseFour.SizeFour),
        ) {
            StyledText(textStyle = MaterialTheme.typography.h6) {
                Text(
                    text = "Ordenar"
                )
            }
            Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeFour))
            LazyColumn {
                items(sortItems) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectedItem(it) },
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeFour))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StyledText(textStyle = MaterialTheme.typography.subtitle1) {
                                Text(text = getTextFromSortType(it.sortItemType))
                            }
                            if (it.isSelected) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            }
                        }
                        Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeFour))
                    }
                }
            }
        }
    }
}

private fun getTextFromSortType(sortItemType: SortItemType) =
    when (sortItemType) {
        SortItemType.UNSELECTED_SORT -> "Sem ordenação"
        SortItemType.COMPLETED_TASKS -> "Completas"
        SortItemType.UNCOMPLETED_TASKS -> "Incompletas"
    }

@Preview(showBackground = true)
@Composable
private fun SortTaskListComponentPreview() {
    ApplicationTheme {
        SortTaskListComponent(
            listOf(
                SortItem(SortItemType.UNSELECTED_SORT, true),
                SortItem(SortItemType.UNCOMPLETED_TASKS, false),
                SortItem(SortItemType.COMPLETED_TASKS, false),
            ),
            {}
        )
    }
}
