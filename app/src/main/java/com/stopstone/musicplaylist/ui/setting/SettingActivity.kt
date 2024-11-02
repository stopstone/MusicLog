package com.stopstone.musicplaylist.ui.setting

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.ActivitySettingBinding
import com.stopstone.musicplaylist.util.showToast
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
                        showToast(getString(R.string.label_track_delete))
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        showToast(getString(R.string.label_track_delete_failed))
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
        MaterialAlertDialogBuilder(this).apply {
            setTitle(getString(R.string.delete_track_title))
            setMessage(getString(R.string.delete_track_message))
            setNegativeButton(R.string.label_cancel, null)
            setPositiveButton(R.string.description_track_delete_button) { _, _ ->
                viewModel.clearData()
            }
            show()
        }

    }
}