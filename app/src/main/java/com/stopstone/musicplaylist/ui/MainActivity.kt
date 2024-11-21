package com.stopstone.musicplaylist.ui

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.ActivityMainBinding
import com.stopstone.musicplaylist.util.showSnackBarWithNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var lastBackPressedTime: Long = 0L

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastBackPressedTime < BACK_PRESS_INTERVAL) {
                moveTaskToBack(true)
            } else {
                lastBackPressedTime = currentTime
                binding.root.showSnackBarWithNavigation(
                    getString(R.string.message_press_back_to_exit),
                    binding.bottomNavigationHome
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initBottomNavigation()
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun initBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_home) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationHome.setupWithNavController(navController)
    }

    companion object {
        private const val BACK_PRESS_INTERVAL: Long = 2000L
    }
}