package com.stopstone.musicplaylist.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.ActivityLoginBinding
import com.stopstone.musicplaylist.ui.MainActivity
import com.stopstone.musicplaylist.ui.login.auth.SocialLoginHandler
import com.stopstone.musicplaylist.ui.login.model.ProviderType
import com.stopstone.musicplaylist.ui.login.viewmodel.LoginUiState
import com.stopstone.musicplaylist.ui.login.viewmodel.LoginViewModel
import com.stopstone.musicplaylist.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val viewModel: LoginViewModel by viewModels()

    private val socialLoginHandler: SocialLoginHandler by lazy {
        SocialLoginHandler(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        viewModel.checkAutoLogin()
        setupWindowInsets()
        setupListeners()
        observeViewModel()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        with(binding) {
            btnKakaoLogin.setOnClickListener {
                performKakaoLogin()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is LoginUiState.Idle -> {
                            hideLoading()
                        }

                        is LoginUiState.AutoLoginSuccess -> {
                            navigateToMain()
                        }

                        is LoginUiState.ShowLoginScreen -> {
                            hideLoading()
                        }

                        is LoginUiState.Loading -> {
                            showLoading()
                        }

                        is LoginUiState.Success -> {
                            hideLoading()
                            showToast(getString(R.string.login_success))
                            navigateToMain()
                        }

                        is LoginUiState.SuccessWithSyncError -> {
                            hideLoading()
                            showToast(getString(R.string.login_success_sync_failed))
                            navigateToMain()
                        }

                        is LoginUiState.Error -> {
                            hideLoading()
                            showToast(state.message ?: getString(R.string.login_unknown_error))
                        }
                    }
                }
            }
        }
    }

    /**
     * 카카오 로그인 수행
     */
    private fun performKakaoLogin() {
        lifecycleScope.launch {
            socialLoginHandler.performSocialLogin(
                providerType = ProviderType.KAKAO,
                onSuccess = { userId ->
                    viewModel.onLoginSuccess(userId)
                },
                onFailure = { error ->
                    viewModel.onLoginFailure(error.message ?: getString(R.string.login_sdk_error))
                }
            )
        }
    }

    private fun showLoading() {
        with(binding.layoutLoading.loadingContainer) {
            visibility = View.VISIBLE
            isEnabled = false
        }
    }

    private fun hideLoading() {
        with(binding.layoutLoading.loadingContainer) {
            visibility = View.GONE
            isEnabled = true
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
