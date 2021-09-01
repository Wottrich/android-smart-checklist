package wottrich.github.io.androidsmartchecklist.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.databinding.DataBindingUtil
import androidx.navigation.ui.setupWithNavController
import com.example.androidsmartchecklist.R
import com.example.androidsmartchecklist.databinding.ActivityHomeBinding
import org.koin.androidx.viewmodel.compat.SharedViewModelCompat.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import wottrich.github.io.androidsmartchecklist.ComposeActivity
import wottrich.github.io.androidsmartchecklist.ui.HomeScaffold
import wottrich.github.io.components.RowComponent
import wottrich.github.io.components.SubtitleRow
import wottrich.github.io.components.TitleRow
import wottrich.github.io.components.ui.ApplicationTheme
import wottrich.github.io.featurenew.view.NewChecklistActivity
import wottrich.github.io.tools.extensions.navHost
import wottrich.github.io.tools.extensions.startActivity


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel by viewModel<HomeViewModel>()
    private val navController by lazy { navHost(R.id.navControllerContainer).navController }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                HomeScaffold(onFloatingActionButtonClick = { startNewChecklistActivity() }) {
                    BuildChecklists()
                }
            }
        }
    }

    @Composable
    private fun BuildChecklists() {
        val state by viewModel.homeStateFlow.collectAsState()
        when (state) {
            is HomeState.Success -> {
                LazyColumn(content = {
                    items((state as HomeState.Success).checklists) {
                        RowComponent(
                            leftContent = {
                                TitleRow(text = it.name)
                                SubtitleRow(text = it.latestUpdateFormatted.orEmpty())
                            },
                            rightIconContent = {
                                Icon(
                                    tint = MaterialTheme.colors.onSurface,
                                    painter = painterResource(id = R.drawable.ic_round_arrow_right),
                                    contentDescription = "Ir para items do checklist"
                                )
                            }
                        )
                    }
                })
            }
            else -> Unit
        }
    }

    private fun setupBottomAppBarWithNavController() {
        binding.bottomAppBar.setupWithNavController(navController)
    }

    private fun startNewChecklistActivity() {
        NewChecklistActivity.start(this)
    }
}