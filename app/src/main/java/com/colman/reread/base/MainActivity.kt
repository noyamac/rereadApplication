package com.colman.reread.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.colman.reread.R
import com.colman.reread.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        binding.bottomNavigation.setupWithNavController(navController)

        val authDestinations = setOf(R.id.signInFragment, R.id.signUpFragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in authDestinations) {
                binding.appBar.visibility = View.GONE
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.appBar.visibility = View.VISIBLE
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
    }
}
