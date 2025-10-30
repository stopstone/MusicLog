package com.stopstone.musicplaylist.ui.music_search

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stopstone.musicplaylist.databinding.ActivityMusicSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicSearchActivity : AppCompatActivity() {
    private val binding: ActivityMusicSearchBinding by lazy { ActivityMusicSearchBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupWindowInsets()
        setContentView(binding.root)
        setListeners()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom,
            )
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setListeners() {
        with(binding) {
            searchToolbar.setNavigationOnClickListener {
                finish()
            }
        }
    }
}
