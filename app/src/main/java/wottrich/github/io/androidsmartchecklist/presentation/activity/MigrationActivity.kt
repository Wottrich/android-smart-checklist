package wottrich.github.io.androidsmartchecklist.presentation.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import org.koin.androidx.viewmodel.ext.android.viewModel
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.MigrationUiEffect.MigrationSuccessful
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.MigrationViewModel
import wottrich.github.io.baseui.ui.ApplicationTheme
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
                    Text(text = "We are doing some adjustments...")
                }
            }
        }
    }

}