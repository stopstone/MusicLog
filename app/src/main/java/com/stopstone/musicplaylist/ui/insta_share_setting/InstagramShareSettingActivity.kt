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
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.lifecycleScope
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.ActivityInstagramShareSettingBinding
import com.stopstone.musicplaylist.util.performHaptic
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

            layoutEmotionsRow.setOnClickListener {
                switchShowEmotions.toggle()
            }

            layoutMemoRow.setOnClickListener {
                switchShowMemo.toggle()
            }

            layoutRecordedTimeRow.setOnClickListener {
                switchShowRecordedTime.toggle()
            }

            switchShowEmotions.setOnCheckedChangeListener { view, isChecked ->
                if (viewModel.uiState.value.isLoadingFromDataStore) return@setOnCheckedChangeListener
                view.performHaptic()
                viewModel.updateShowEmotions(isChecked)
            }
            switchShowMemo.setOnCheckedChangeListener { view, isChecked ->
                if (viewModel.uiState.value.isLoadingFromDataStore) return@setOnCheckedChangeListener
                view.performHaptic()
                viewModel.updateShowMemo(isChecked)
            }
            switchShowRecordedTime.setOnCheckedChangeListener { view, isChecked ->
                if (viewModel.uiState.value.isLoadingFromDataStore) return@setOnCheckedChangeListener
                view.performHaptic()
                viewModel.updateShowRecordedTime(isChecked)
            }
        }
    }

    private fun setupPreview() {
        with(binding.layoutPreview) {
            tvStoryTitle.text = getString(R.string.label_instagram_share_preview_title)
            tvStoryArtist.text = getString(R.string.label_instagram_share_preview_artist)
            tvStoryMemo.text = getString(R.string.label_instagram_share_preview_memo)
            ivStoryAlbumCover.setBackgroundColor(getColor(R.color.gray_400))
            seekbarStoryTime.progress = SAMPLE_RECORDED_TIME_MINUTES
            tvStoryTime.text = SAMPLE_RECORDED_TIME_TEXT
            addSampleEmotionChips()
        }
    }

    private fun addSampleEmotionChips() {
        val sampleEmotions =
            listOf(
                getString(R.string.emotion_joyful),
                getString(R.string.emotion_loving),
                getString(R.string.emotion_peaceful),
            )
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
                    TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MusicLog_BodyMedium)
                }
            binding.layoutPreview.chipGroupStoryEmotions.addView(textView)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                binding.switchShowEmotions.isChecked = uiState.showEmotions
                binding.switchShowMemo.isChecked = uiState.showMemo
                binding.switchShowRecordedTime.isChecked = uiState.showRecordedTime

                updateEmotionsPreview(uiState.showEmotions)
                updateMemoPreview(uiState.showMemo)
                updateRecordedTimePreview(uiState.showRecordedTime)
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

    private fun updateRecordedTimePreview(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        binding.layoutPreview.seekbarStoryTime.visibility = visibility
        binding.layoutPreview.tvStoryTime.visibility = visibility
    }

    companion object {
        private const val SAMPLE_RECORDED_TIME_MINUTES: Int = 22 * 60 + 30
        private const val SAMPLE_RECORDED_TIME_TEXT = "22:30 / 24:00"
    }
}
