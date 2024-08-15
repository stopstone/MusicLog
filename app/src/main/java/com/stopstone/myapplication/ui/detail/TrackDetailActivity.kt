package com.stopstone.myapplication.ui.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import com.stopstone.myapplication.databinding.ActivityTrackDetailBinding
import com.stopstone.myapplication.ui.detail.viewmodel.TrackDetailViewModel
import com.stopstone.myapplication.util.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class TrackDetailActivity : AppCompatActivity() {
    private val binding: ActivityTrackDetailBinding by lazy { ActivityTrackDetailBinding.inflate(layoutInflater) }
    private val args: TrackDetailActivityArgs by navArgs()
    private val viewModel: TrackDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val date = with(args.DailyTrack) {
            val day = args.DailyTrack.id
            val year = args.DailyTrack.year
            val month = args.DailyTrack.month
            convertDayToDate(year, month, day)
        }

        with(binding) {
            val track = args.DailyTrack.track!!
            ivTrackDetailAlbumCover.loadImage(track.imageUrl)
            tvTrackDetailTitle.text = track.title
            tvTrackDetailArtist.text = track.artist

            btnTrackDetailSave.setOnClickListener {
                viewModel.updateComment(etTrackDetailComment.text.toString())
            }
        }

        viewModel.setCurrentDate(date)

        lifecycleScope.launch {
            viewModel.comment.collectLatest { comment ->
                if (comment != binding.etTrackDetailComment.text.toString()) {
                    binding.etTrackDetailComment.setText(comment)
                    binding.etTrackDetailComment.setSelection(comment.length)
                }
            }
        }
    }

    private fun convertDayToDate(year: Int, month: Int, day: Int): Date {
        return Calendar.getInstance().apply {
            set(year, month, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
}