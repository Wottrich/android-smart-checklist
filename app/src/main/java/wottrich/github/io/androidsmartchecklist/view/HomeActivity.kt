package wottrich.github.io.androidsmartchecklist.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.androidsmartchecklist.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import wottrich.github.io.androidsmartchecklist.ui.HomeScaffold
import wottrich.github.io.components.RowComponent
import wottrich.github.io.components.SubtitleRow
import wottrich.github.io.components.TitleRow
import wottrich.github.io.components.ui.ApplicationTheme
import wottrich.github.io.database.entity.Checklist
import wottrich.github.io.featurenew.view.NewChecklistActivity


class HomeActivity : AppCompatActivity() {

    private val viewModel by viewModel<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                HomeScaffold(onFloatingActionButtonClick = { startNewChecklistActivity() }) {
                    BuildChecklists { checklistId ->
                        NewChecklistActivity.startEditFlow(this, checklistId)
                    }
                }
            }
        }
    }

    @Composable
    private fun BuildChecklists(onItemClick: (checklistId: Long) -> Unit) {
        val state by viewModel.homeStateFlow.collectAsState()
        when {
            state.isLoading -> CircularProgressIndicator()
            else -> HomeSuccessContent(checklists = state.checklists, onItemClick = onItemClick)
        }
    }

    @Composable
    private fun HomeSuccessContent(
        checklists: List<Checklist>,
        onItemClick: (checklistId: Long) -> Unit
    ) {
        LazyColumn(content = {
            items(checklists) {
                HomeChecklistItem(checklist = it, onItemClick = onItemClick)
            }
        })
    }

    @Composable
    fun HomeChecklistItem(checklist: Checklist, onItemClick: (checklistId: Long) -> Unit) {
        val modifier = Modifier.clickable(enabled = checklist.checklistId != null) {
            checklist.checklistId?.let(onItemClick)
        }
        RowComponent(
            modifier = modifier,
            leftContent = {
                TitleRow(text = checklist.name)
                SubtitleRow(text = checklist.latestUpdateFormatted.orEmpty())
            },
            rightIconContent = {
                Icon(
                    tint = MaterialTheme.colors.onSurface,
                    painter = painterResource(id = R.drawable.ic_round_arrow_right),
                    contentDescription = stringResource(
                        R.string.navigate_to_checklist_detail, checklist.name
                    )
                )
            }
        )
    }

    private fun startNewChecklistActivity() {
        NewChecklistActivity.start(this)
    }
}