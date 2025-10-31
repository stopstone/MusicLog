package com.stopstone.musicplaylist.ui.music_memo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.ActivityMusicMemoBinding
import com.stopstone.musicplaylist.ui.model.TrackUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicMemoActivity : AppCompatActivity() {
    private val binding: ActivityMusicMemoBinding by lazy {
        ActivityMusicMemoBinding.inflate(
            layoutInflater,
        )
    }

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
