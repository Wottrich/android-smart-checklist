package wottrich.github.io.featurenew.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import wottrich.github.io.featurenew.R
import wottrich.github.io.featurenew.databinding.ActivityNewChecklistBinding
import wottrich.github.io.tools.extensions.navHost

class NewChecklistActivity : AppCompatActivity(), AppBarConfiguration.OnNavigateUpListener {

    private lateinit var binding: ActivityNewChecklistBinding
    private val navHost by lazy { navHost(R.id.navControllerContainer) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_checklist)

        setupActionBar()
    }

    private fun setupActionBar () {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupActionBarWithNavController(navHost.navController, setupAppBarConfiguration())
    }

    private fun setupAppBarConfiguration(): AppBarConfiguration {
        return AppBarConfiguration.Builder()
            .setFallbackOnNavigateUpListener(this)
            .build()
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

}