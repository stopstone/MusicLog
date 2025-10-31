package com.stopstone.musicplaylist.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        enableEdgeToEdge()
        setContentView(binding.root)
        setupWindowInsets()
        initBottomNavigation()
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // BottomNavigationView에 하단 패딩 적용
            with(binding.bottomNavigationHome) {
                setPadding(
                    paddingLeft,
                    paddingTop,
                    paddingRight,
                    insets.bottom
                )
            }
            
            // FragmentContainerView에 상단 패딩 적용
            with(binding.containerHome) {
                setPadding(
                    paddingLeft,
                    insets.top,
                    paddingRight,
                    paddingBottom
                )
            }
            
            windowInsets
        }
    }

    private fun initBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_home) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationHome.setupWithNavController(navController)
    }

    companion object {
        private const val BACK_PRESS_INTERVAL: Long = 2000L

        fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        }
    }
}