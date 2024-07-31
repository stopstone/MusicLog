package com.stopstone.myapplication.ui.view.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.navArgs
import com.stopstone.myapplication.R
import com.stopstone.myapplication.databinding.ActivityTrackDetailBinding
import com.stopstone.myapplication.util.loadImage

class TrackDetailActivity : AppCompatActivity() {
    private val binding: ActivityTrackDetailBinding by lazy { ActivityTrackDetailBinding.inflate(layoutInflater) }
    private val args: TrackDetailActivityArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val track = args.DailyTrack.track!!
        with(binding) {
            ivTrackDetailAlbumCover.loadImage(track.imageUrl)
            tvTrackDetailTitle.text = track.title
            tvTrackDetailArtist.text = track.artist
        }
    }
}