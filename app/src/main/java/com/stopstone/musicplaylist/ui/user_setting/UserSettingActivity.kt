package com.stopstone.musicplaylist.ui.user_setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.ActivityUserSettingBinding
import com.stopstone.musicplaylist.ui.login.LoginActivity
import com.stopstone.musicplaylist.ui.login.model.ProviderType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserSettingActivity : AppCompatActivity() {
    private val binding: ActivityUserSettingBinding by lazy {
        ActivityUserSettingBinding.inflate(layoutInflater)
    }

    private val viewModel: UserSettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupLayout()
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                viewModel.uiState
                    .map { it.email }
                    .distinctUntilChanged()
                    .collect { email ->
                        binding.tvUserSettingAccountInfo.text = email
                    }
            }
            launch {
                viewModel.uiState
                    .map { it.providerType }
                    .distinctUntilChanged()
                    .collect { providerType ->
                        updateProviderType(providerType)
                    }
            }
            launch {
                viewModel.uiState
                    .map { it.isLoading }
                    .distinctUntilChanged()
                    .collect { isLoading ->
                        if (isLoading) {
                            showLoading()
                        } else {
                            hideLoading()
                        }
                    }
            }
            launch {
                viewModel.uiState
                    .map { it.isAccountDeleted }
                    .distinctUntilChanged()
                    .collect { isDeleted ->
                        if (isDeleted) {
                            navigateToLogin()
                        }
                    }
            }
        }
    }

    private fun updateProviderType(providerType: ProviderType) {
        if (providerType.isVisible) {
            binding.ivProviderBadge.visibility = View.VISIBLE
            binding.ivProviderBadge.setImageResource(providerType.badgeIconRes)
        } else {
            binding.ivProviderBadge.visibility = View.GONE
        }
    }

    fun setupLayout() {
        with(binding) {
            tvUserSettingLogout.setOnClickListener {
                performLogout()
            }
            tvUserSettingWithdraw.setOnClickListener {
                showDeleteAccountDialog()
            }
        }
    }

    private fun showDeleteAccountDialog() {
        AlertDialog
            .Builder(this)
            .setTitle("회원 탈퇴")
            .setMessage("회원 탈퇴하면 모든 정보가 지워집니다.\n정말 탈퇴하시겠습니까?")
            .setPositiveButton("탈퇴") { _, _ ->
                performDeleteAccount()
            }.setNegativeButton("취소", null)
            .show()
    }

    private fun performDeleteAccount() {
        viewModel.deleteAccount()
    }

    private fun performLogout() {
        viewModel.logout()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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
}
