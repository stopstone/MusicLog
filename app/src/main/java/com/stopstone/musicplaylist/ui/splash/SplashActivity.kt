package com.stopstone.musicplaylist.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.ActivitySplashBinding
import com.stopstone.musicplaylist.ui.MainActivity
import com.stopstone.musicplaylist.ui.splash.viewmodel.SplashViewModel
import com.stopstone.musicplaylist.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val binding: ActivitySplashBinding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(binding.root)
        checkTokenAndNavigateActivity()
    }

    private fun checkTokenAndNavigateActivity() {
        lifecycleScope.launch {
            viewModel.token.collect { token ->
                when(token) {
                    null -> handleError()
                    else -> navigateToMain()
                }
            }
        }
    }

    private suspend fun handleError() {
        showToast(getString(R.string.message_check_internet_status))
        delay(DELAY_TIME)
        finish()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        const val DELAY_TIME = 2000L
    }
}