package wottrich.github.io.featurenew.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import wottrich.github.io.featurenew.R
import wottrich.github.io.featurenew.databinding.ActivityNewChecklistBinding

class NewChecklistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewChecklistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_checklist)

        setupListeners()
        setupActionBar()
    }

    private fun setupActionBar () {
        setSupportActionBar(binding.toolbar)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.navControllerContainer) as NavHostFragment

        val navController = navHost.navController
        setupActionBarWithNavController(navController)
    }

    private fun setupListeners() {
        binding.toolbar.apply {
            setNavigationOnClickListener {
                finish()
            }
        }
    }
}