package com.stopstone.myapplication.ui

import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.stopstone.myapplication.R
import com.stopstone.myapplication.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val doubleBackPressInterval: Long = 2000L
    private var lastBackPressedTime: Long = 0L
    private lateinit var backPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initBottomNavigation()
        onBackPressCallback()
    }

    private fun initBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_home) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationHome.setupWithNavController(navController)
    }

    private fun onBackPressCallback() {
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentTime = SystemClock.elapsedRealtime()
                if (currentTime - lastBackPressedTime < doubleBackPressInterval) {
                    moveTaskToBack(true)
                } else {
                    lastBackPressedTime = currentTime
                    Toast.makeText(
                        this@MainActivity,
                        "뒤로 가기 버튼을 한 번 더 누르면 앱이 백그라운드로 이동합니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}