package wottrich.github.io.androidsmartchecklist.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.androidsmartchecklist.R
import com.example.androidsmartchecklist.databinding.ActivityHomeBinding
import wottrich.github.io.featurenew.view.NewChecklistActivity


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.navControllerContainer) as NavHostFragment

        val navController = navHost.navController
        binding.bottomAppBar.setupWithNavController(navController)

        setupListeners()
    }

    private fun setupListeners () {
        binding.fabNewChecklist.setOnClickListener {
            startActivity(Intent(this, NewChecklistActivity::class.java))
        }
    }
}