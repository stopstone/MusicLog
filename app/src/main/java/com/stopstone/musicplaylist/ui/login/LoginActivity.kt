package com.stopstone.musicplaylist.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.ActivityLoginBinding
import com.stopstone.musicplaylist.ui.MainActivity
import com.stopstone.musicplaylist.ui.login.auth.SocialLoginHandler
import com.stopstone.musicplaylist.ui.login.model.ProviderType
import com.stopstone.musicplaylist.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val socialLoginHandler: SocialLoginHandler by lazy {
        SocialLoginHandler(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupWindowInsets()
        setupListeners()
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

    private fun performKakaoLogin() {
        lifecycleScope.launch {
            socialLoginHandler.performSocialLogin(
                providerType = ProviderType.KAKAO,
                onSuccess = { accessToken ->
                    navigateToMain()
                },
                onFailure = { error ->
                    showToast(error.message ?: getString(R.string.login_sdk_error))
                }
            )
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
