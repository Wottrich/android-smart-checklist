package wottrich.github.io.featurenew.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.ui.setupActionBarWithNavController
import wottrich.github.io.featurenew.R
import wottrich.github.io.featurenew.databinding.ActivityNewChecklistBinding
import wottrich.github.io.tools.extensions.navHost

class NewChecklistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewChecklistBinding
    private val navHost by lazy { navHost(R.id.navControllerContainer) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_checklist)

        setupListeners()
        setupActionBar()
    }

    private fun setupActionBar () {
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navHost.navController)
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}