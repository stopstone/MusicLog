package com.stopstone.musicplaylist.ui.insta_share_setting

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.ActivityInstagramShareSettingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InstagramShareSettingActivity : AppCompatActivity() {
    private val binding: ActivityInstagramShareSettingBinding by lazy {
        ActivityInstagramShareSettingBinding.inflate(layoutInflater)
    }

    private val viewModel: InstagramShareSettingViewModel by viewModels()

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
        setupPreview()
        observeViewModel()
    }

    private fun setupLayout() {
        with(binding) {
            toolbarInstagramShareSetting.setNavigationOnClickListener {
                finish()
            }
            switchShowEmotions.setOnCheckedChangeListener { _, isChecked ->
                if (viewModel.uiState.value.isLoadingFromDataStore) return@setOnCheckedChangeListener
                viewModel.updateShowEmotions(isChecked)
            }
            switchShowMemo.setOnCheckedChangeListener { _, isChecked ->
                if (viewModel.uiState.value.isLoadingFromDataStore) return@setOnCheckedChangeListener
                viewModel.updateShowMemo(isChecked)
            }
        }
    }

    private fun setupPreview() {
        with(binding.layoutPreview) {
            tvStoryTitle.text = "노래 제목"
            tvStoryArtist.text = "아티스트"
            tvStoryMemo.text = "오늘 하루의 감정을 남겨보세요"
            ivStoryAlbumCover.setBackgroundColor(getColor(R.color.gray_400))
            addSampleEmotionChips()
        }
    }

    private fun addSampleEmotionChips() {
        val sampleEmotions = listOf("기쁨", "사랑", "평화")
        sampleEmotions.forEach { emotion ->
            val textView =
                TextView(this).apply {
                    text = emotion
                    background =
                        AppCompatResources.getDrawable(
                            this@InstagramShareSettingActivity,
                            R.drawable.background_gray,
                        )
                    setPadding(16, 8, 16, 8)
                }
            binding.layoutPreview.chipGroupStoryEmotions.addView(textView)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                binding.switchShowEmotions.isChecked = uiState.showEmotions
                binding.switchShowMemo.isChecked = uiState.showMemo

                updateEmotionsPreview(uiState.showEmotions)
                updateMemoPreview(uiState.showMemo)
            }
        }
    }

    private fun updateEmotionsPreview(show: Boolean) {
        binding.layoutPreview.chipGroupStoryEmotions.visibility =
            if (show) View.VISIBLE else View.GONE
    }

    private fun updateMemoPreview(show: Boolean) {
        binding.layoutPreview.tvStoryMemo.visibility = if (show) View.VISIBLE else View.GONE
    }
}
