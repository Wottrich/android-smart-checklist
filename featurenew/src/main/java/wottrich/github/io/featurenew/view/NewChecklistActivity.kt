package wottrich.github.io.featurenew.view

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import wottrich.github.io.featurenew.R
import wottrich.github.io.featurenew.databinding.ActivityNewChecklistBinding
import wottrich.github.io.tools.extensions.navHost
import wottrich.github.io.tools.extensions.startActivity

class NewChecklistActivity : AppCompatActivity(), AppBarConfiguration.OnNavigateUpListener {

    private lateinit var binding: ActivityNewChecklistBinding
    private val navHost by lazy { navHost(R.id.navControllerContainer) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_checklist)
        navHost.apply {
            navController.apply {
                val graph = navInflater.inflate(R.navigation.nav_new_checklist)
                graph.startDestination = getStartDestination()
                setGraph(graph, intent.extras)
            }
        }

        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupActionBarWithNavController(navHost.navController, setupAppBarConfiguration())
    }

    private fun setupAppBarConfiguration(): AppBarConfiguration {
        return AppBarConfiguration.Builder()
            .setFallbackOnNavigateUpListener(this)
            .build()
    }

    private fun getStartDestination(): Int {
        return if (intent.getBooleanExtra(INTENT_EDIT_TASK, false)) {
            R.id.taskListFragment
        } else {
            R.id.checklistNameFragment
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val INTENT_EDIT_TASK = "INTENT_EDIT_TASK"
        private const val INTENT_CHECKLIST_ID = "checklistId"

        fun start(activity: Activity) {
            activity.startActivity<NewChecklistActivity>()
        }

        fun startEditFlow(activity: Activity, checkListId: Long) {
            activity.startActivity<NewChecklistActivity> {
                putExtra(INTENT_EDIT_TASK, true)
                putExtra(INTENT_CHECKLIST_ID, checkListId)
            }
        }

    }

}