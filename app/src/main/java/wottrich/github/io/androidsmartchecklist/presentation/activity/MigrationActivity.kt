package wottrich.github.io.androidsmartchecklist.presentation.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import org.koin.androidx.viewmodel.ext.android.viewModel
import wottrich.github.io.androidsmartchecklist.R
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.MigrationUiEffect.MigrationSuccessful
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.MigrationViewModel
import wottrich.github.io.baseui.TextOneLine
import wottrich.github.io.baseui.ui.ApplicationTheme
import wottrich.github.io.baseui.ui.RowDefaults
import wottrich.github.io.baseui.ui.TextStateComponent
import wottrich.github.io.tools.extensions.startActivity

@OptIn(InternalCoroutinesApi::class)
class MigrationActivity : AppCompatActivity() {

    private val viewModel by viewModel<MigrationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val effects = viewModel.uiEffect
            LaunchedEffect(key1 = effects) {
                effects.collect(FlowCollector {
                    when (it) {
                        MigrationSuccessful -> startActivity<HomeActivity>(finishActualActivity = true)
                    }
                })
            }
            ApplicationTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TextOneLine(
                        primary = {
                            TextStateComponent(
                                textState = RowDefaults.title(
                                    text = stringResource(id = R.string.migration_loading)
                                )
                            )
                        }
                    )
                }
            }
        }
    }

}