package wottrich.github.io.androidsmartchecklist.presentation.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import github.io.wottrich.checklist.presentation.activity.NewChecklistActivity
import kotlinx.coroutines.InternalCoroutinesApi
import wottrich.github.io.androidsmartchecklist.presentation.ui.content.HomeScreen
import wottrich.github.io.baseui.ui.ApplicationTheme
import wottrich.github.io.tools.extensions.shareIntentText

@InternalCoroutinesApi
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                HomeScreen(
                    onAddNewChecklist = ::startNewChecklistActivity,
                    onCopyChecklist = {
                        shareIntentText(it)
                    }
                )
            }
        }
    }

    private fun startNewChecklistActivity() {
        NewChecklistActivity.launch(this)
    }
}