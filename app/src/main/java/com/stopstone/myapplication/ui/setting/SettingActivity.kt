package com.stopstone.myapplication.ui.setting

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stopstone.myapplication.R
import com.stopstone.myapplication.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private val binding: ActivitySettingBinding by lazy { ActivitySettingBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }


    fun setListeners() {
        binding.toolbarSetting.setNavigationOnClickListener {
        }

        binding.toolbarSetting.setNavigationOnClickListener {
            finish()
        }

    }
}