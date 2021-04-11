package wottrich.github.io.androidsmartchecklist.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.ui.setupWithNavController
import com.example.androidsmartchecklist.R
import com.example.androidsmartchecklist.databinding.ActivityHomeBinding
import wottrich.github.io.androidsmartchecklist.ComposeActivity
import wottrich.github.io.featurenew.view.NewChecklistActivity
import wottrich.github.io.tools.extensions.navHost
import wottrich.github.io.tools.extensions.startActivity


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val navController by lazy { navHost(R.id.navControllerContainer).navController }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        setupBottomAppBarWithNavController()
        setupListeners()

        startActivity<ComposeActivity>()
    }

    private fun setupBottomAppBarWithNavController () {
        binding.bottomAppBar.setupWithNavController(navController)
    }

    private fun setupListeners () {
        binding.fabNewChecklist.setOnClickListener {
            NewChecklistActivity.start(this)
        }
    }
}