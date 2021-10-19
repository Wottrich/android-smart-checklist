package wottrich.github.io.androidsmartchecklist.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.androidsmartchecklist.R
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel
import wottrich.github.io.androidsmartchecklist.ui.HomeScaffold
import wottrich.github.io.components.RowComponent
import wottrich.github.io.components.SubtitleRow
import wottrich.github.io.components.TitleRow
import wottrich.github.io.components.ui.ApplicationTheme
import wottrich.github.io.components.ui.Sizes
import wottrich.github.io.database.entity.Checklist
import wottrich.github.io.featurenew.view.NewChecklistActivity


@InternalCoroutinesApi
class HomeActivity : AppCompatActivity() {

    private val viewModel by viewModel<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                HomeScaffold(onFloatingActionButtonClick = { startNewChecklistActivity() }) {
                    BuildChecklists { checklistId ->
                        Toast.makeText(this, "WIP", Toast.LENGTH_SHORT).show()
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
            item {
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp))
            }
        })
    }

    @Composable
    fun HomeChecklistItem(checklist: Checklist, onItemClick: (checklistId: Long) -> Unit) {
        val surfaceModifier = Modifier.padding(
            top = Sizes.x8,
            start = Sizes.x8,
            end = Sizes.x8
        )
        val rowModifier = Modifier
            .clip(RoundedCornerShape(Sizes.x8))
            .clickable(enabled = checklist.checklistId != null) {
                checklist.checklistId?.let(onItemClick)
            }
        Surface(
            modifier = surfaceModifier,
            shape = RoundedCornerShape(Sizes.x8),
            elevation = Sizes.x4
        ) {
            RowComponent(
                modifier = rowModifier,
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
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(Sizes.x8))
    }

    private fun startNewChecklistActivity() {
        NewChecklistActivity.launch(this)
        //NewChecklistActivity.start(this)
    }
}