package com.stopstone.myapplication.ui.setting

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.stopstone.myapplication.databinding.ActivitySettingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {
    private val binding: ActivitySettingBinding by lazy { ActivitySettingBinding.inflate(layoutInflater) }
    private val viewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setListeners()
    }


    private fun setListeners() {
        binding.tvDataClear.setOnClickListener {
            viewModel.clearData()
        }

        binding.toolbarSetting.setNavigationOnClickListener {
            finish()
        }
    }
}