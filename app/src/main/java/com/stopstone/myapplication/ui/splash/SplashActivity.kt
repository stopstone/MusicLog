package com.stopstone.myapplication.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.stopstone.myapplication.databinding.ActivitySplashBinding
import com.stopstone.myapplication.ui.MainActivity
import com.stopstone.myapplication.ui.splash.viewmodel.SplashViewModel
import com.stopstone.myapplication.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(
            layoutInflater
        )
    }
    private val viewModel: SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(binding.root)
        viewModel.getToken()

        lifecycleScope.launch {
            viewModel.token.collect { token ->
                token?.let {
                    Log.d("SplashActivity", "토큰이 null이 아닙니다: $token")
                    navigateToMain()
                } ?: run {
                    showToast("인터넷 상태를 확인해주세요.")
                    delay(2000)
                    finish()
                }
            }
        }
    }


    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}