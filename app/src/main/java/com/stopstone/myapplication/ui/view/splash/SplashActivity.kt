package com.stopstone.myapplication.ui.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.stopstone.myapplication.databinding.ActivitySplashBinding
import com.stopstone.myapplication.ui.view.MainActivity
import com.stopstone.myapplication.ui.viewmodel.SplashViewModel
import com.stopstone.myapplication.ui.viewmodel.TokenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val binding: ActivitySplashBinding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        observeTokenState()
    }

    private fun observeTokenState() = lifecycleScope.launch {
        viewModel.tokenState.collect { state ->
            when (state) {
                is TokenState.Initial -> {
                    Log.d(TAG, "토큰 상태: 로딩 중")
                }

                is TokenState.Success -> {
                    Log.d(TAG, "토큰 상태: 성공")
                    navigateToMain(state.token)
                }

                is TokenState.Error -> {
                    Log.e(TAG, "토큰 상태: 오류 - ${state.message}")
                    viewModel.clearToken()
                }
            }
        }
    }

    private fun navigateToMain(token: String) {
        Log.d(TAG, "토큰과 함께 MainActivity로 이동: $token")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        private const val TAG = "SplashActivity"
    }
}
