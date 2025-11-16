package com.stopstone.musicplaylist.ui.music_memo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.Chip
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.ActivityMusicMemoBinding
import com.stopstone.musicplaylist.ui.MainActivity
import com.stopstone.musicplaylist.ui.model.TrackUiState
import com.stopstone.musicplaylist.ui.music_memo.viewmodel.MusicMemoViewModel
import com.stopstone.musicplaylist.util.loadImage
import com.stopstone.musicplaylist.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MusicMemoActivity : AppCompatActivity() {
    private val binding: ActivityMusicMemoBinding by lazy {
        ActivityMusicMemoBinding.inflate(
            layoutInflater,
        )
    }

    private val viewModel: MusicMemoViewModel by viewModels()

    private val track: TrackUiState? by lazy {
        intent.getParcelableExtra(EXTRA_TRACK)
    }

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
        setupListeners()
        setupObservers()
    }

    private fun setupLayout() {
        with(binding) {
            memoToolbar.setNavigationOnClickListener {
                finish()
            }
            layoutMemoTrack.ivTrackImage.loadImage(track?.imageUrl.orEmpty())
            layoutMemoTrack.tvTrackTitle.text = track?.title.orEmpty()
            layoutMemoTrack.tvTrackArtist.text = track?.artist.orEmpty()
        }
    }

    private fun setupListeners() {
        binding.btnSaveMemo.setOnClickListener {
            val currentTrack = track
            if (currentTrack == null) {
                showToast(getString(R.string.message_track_info_not_available))
                return@setOnClickListener
            }
            val comment =
                binding.etSearch.text
                    ?.toString()
                    ?.trim()
            viewModel.saveTrack(currentTrack, comment)
        }
    }

    private fun setupObservers() {
        setupEmotionChips()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.selectedEmotions.collect { selected ->
                        updateChipStates(selected)
                    }
                }
                launch {
                    viewModel.availableEmotions.collect { emotions ->
                        renderEmotionChips(emotions)
                    }
                }
                launch {
                    viewModel.trackSaved.collect { isSaved ->
                        handleTrackSaved(isSaved)
                    }
                }
            }
        }
    }

    private fun setupEmotionChips() {
        binding.cgEmotion.removeAllViews()
    }

    private fun renderEmotionChips(emotions: List<String>) {
        binding.cgEmotion.removeAllViews()
        emotions.forEach { name ->
            val chip = createEmotionChip(name)
            binding.cgEmotion.addView(chip)
        }
        updateChipStates(viewModel.selectedEmotions.value)
    }

    private fun createEmotionChip(emotionName: String): Chip {
        val chip =
            LayoutInflater.from(this).inflate(
                R.layout.item_emotion_chip,
                binding.cgEmotion,
                false,
            ) as Chip
        chip.id = View.generateViewId()
        chip.text = emotionName
        chip.tag = emotionName
        chip.isChecked = viewModel.selectedEmotions.value.contains(emotionName)
        chip.setOnClickListener {
            val name = chip.tag as? String ?: return@setOnClickListener
            val wasToggled = viewModel.toggleEmotion(name)
            if (!wasToggled) {
                chip.isChecked = false
            }
        }
        return chip
    }

    private fun updateChipStates(selectedEmotions: List<String>) {
        for (i in 0 until binding.cgEmotion.childCount) {
            val chip = binding.cgEmotion.getChildAt(i) as? Chip ?: continue
            val emotionName = chip.tag as? String ?: continue
            chip.isChecked = selectedEmotions.contains(emotionName)
        }
    }

    private fun handleTrackSaved(isSaved: Boolean) {
        if (isSaved) {
            showToast(getString(R.string.label_track_saved))
            navigateHome()
        } else {
            showToast(getString(R.string.label_track_save_failed))
        }
    }

    private fun navigateHome() {
        val intent = MainActivity.createIntent(this)
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_TRACK: String = "extra_track"

        fun createIntent(
            context: Context,
            track: TrackUiState,
        ): Intent {
            val intent = Intent(context, MusicMemoActivity::class.java)
            intent.putExtra(EXTRA_TRACK, track)
            return intent
        }
    }
}
