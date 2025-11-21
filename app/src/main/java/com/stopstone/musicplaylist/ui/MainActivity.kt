package com.stopstone.musicplaylist.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.ActivityMainBinding
import com.stopstone.musicplaylist.notification.NotificationScheduler
import com.stopstone.musicplaylist.util.showSnackBarWithNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var lastBackPressedTime: Long = 0L

    // 알림 권한 요청
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                NotificationScheduler.scheduleDailyMusicReminder(this)
            }
        }

    private val backPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentTime = SystemClock.elapsedRealtime()
                if (currentTime - lastBackPressedTime < BACK_PRESS_INTERVAL) {
                    moveTaskToBack(true)
                } else {
                    lastBackPressedTime = currentTime
                    binding.root.showSnackBarWithNavigation(
                        getString(R.string.message_press_back_to_exit),
                        binding.bottomNavigationHome,
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
        checkAndRequestNotificationPermission()
    }

    private fun checkAndRequestNotificationPermission() {
        // Android 13 이상에서만 권한 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS,
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                // 이미 권한 있으면 알림 스케줄링
                NotificationScheduler.scheduleDailyMusicReminder(this)
            } else {
                // 없으면 권한 요청
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Android 12 이하
            NotificationScheduler.scheduleDailyMusicReminder(this)
        }
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
                    insets.bottom,
                )
            }

            // FragmentContainerView에 상단 패딩 적용
            with(binding.containerHome) {
                setPadding(
                    paddingLeft,
                    insets.top,
                    paddingRight,
                    paddingBottom,
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

        fun createIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
    }
}
