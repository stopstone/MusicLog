package com.stopstone.myapplication.ui.setting

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.stopstone.myapplication.databinding.ActivitySettingBinding
import com.stopstone.myapplication.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {
    private val binding: ActivitySettingBinding by lazy { ActivitySettingBinding.inflate(layoutInflater) }
    private val viewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setListeners()
        collectViewModel()
    }

    private fun collectViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.clearDataResult.collect { isCleared ->
                    if (isCleared) {
                        showToast("모든 데이터가 삭제되었습니다.")
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        showToast("데이터 삭제 중 오류가 발생했습니다.")
                    }
                }
            }
        }
    }

    private fun setListeners() {
        binding.tvDataClear.setOnClickListener {
            showDeleteConfirmDialog()
        }

        binding.toolbarSetting.setNavigationOnClickListener {
            finish()
        }
    }

    private fun showDeleteConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("데이터 삭제")
            .setMessage("모든 데이터를 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                viewModel.clearData()
            }
            .setNegativeButton("취소", null)
            .show()
    }
}