package com.stopstone.myapplication.ui.detail

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
        setLayout()
        setListeners()
        collectViewModel()

        Log.d("TrackDetailActivity", "onCreate: ${args.DailyTrack}")
        Log.d("TrackDetailActivity", "onCreate: ${args.DailyTrack.emotions}")

    }

    private fun setLayout() {
        with(args.DailyTrack) {
            convertDayToDate(year, month, id) // 저장된 트랙의 날짜를, date 타입의 시간으로 변경
        }.also { viewModel.setCurrentDate(it) }

        with(binding) {
            val track = args.DailyTrack.track!!
            ivTrackDetailAlbumCover.loadImage(track.imageUrl)
            tvTrackDetailTitle.text = track.title
            tvTrackDetailArtist.text = track.artist
        }
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { collectComment() }
            launch { collectDeleteResult() }
        }
    }

    private suspend fun collectComment() {
        viewModel.comment.collectLatest { comment ->
            binding.etTrackDetailComment.setText(comment)
            binding.etTrackDetailComment.setSelection(comment.length)
        }
    }

    private suspend fun collectDeleteResult() {
        viewModel.deleteResult.collect { isDeleted ->
            if (isDeleted) {
                Toast.makeText(this@TrackDetailActivity, "트랙이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                finish() // 액티비티 종료
            } else {
                Toast.makeText(this@TrackDetailActivity, "삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setListeners() {
        binding.btnTrackDetailSave.setOnClickListener {
            viewModel.updateComment(binding.etTrackDetailComment.text.toString())
        }

        binding.btnTrackDetailDelete.setOnClickListener {
            viewModel.deleteTrack()
        }
    }

    private fun convertDayToDate(year: Int, month: Int, day: Int): Date {
        return Calendar.getInstance().apply {
            set(year, month - 1, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
}