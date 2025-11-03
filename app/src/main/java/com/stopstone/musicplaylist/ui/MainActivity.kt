package com.stopstone.musicplaylist.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.ActivityMainBinding
import com.stopstone.musicplaylist.util.showSnackBarWithNavigation
import com.stopstone.musicplaylist.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModels()
    private var lastBackPressedTime: Long = 0L
    private var isTokenCheckComplete: Boolean = false

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
        installSplashScreen().setKeepOnScreenCondition {
            !isTokenCheckComplete
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        checkTokenAndNavigate()
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

    // 토큰 체크 후 화면 전환 처리
    private fun checkTokenAndNavigate() {
        lifecycleScope.launch {
            // 첫 번째 emit만 처리 (한 번만 실행)
            val token = viewModel.token.first()
            when (token) {
                null -> handleTokenError()
                else -> handleTokenSuccess()
            }
        }
    }

    // 토큰이 없거나 오류가 발생한 경우 처리
    private suspend fun handleTokenError() {
        isTokenCheckComplete = true
        showToast(getString(R.string.message_check_internet_status))
        delay(DELAY_TIME)
        finish()
    }

    // 토큰이 있는 경우 메인 화면으로 진행
    private fun handleTokenSuccess() {
        isTokenCheckComplete = true
    }

    companion object {
        private const val BACK_PRESS_INTERVAL: Long = 2000L
        const val DELAY_TIME = 2000L

        fun createIntent(context: Context): Intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
    }
}
