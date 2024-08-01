package com.stopstone.myapplication.presentation.ui.detail

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import com.stopstone.myapplication.databinding.ActivityTrackDetailBinding
import com.stopstone.myapplication.presentation.viewmodel.TrackDetailViewModel
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

        val track = args.DailyTrack.track!!
        val day = args.DailyTrack.day
        Log.d("TrackDetailActivity", "onCreate: $track")
        Log.d("TrackDetailActivity", "onCreate: $day")

        with(binding) {
            ivTrackDetailAlbumCover.loadImage(track.imageUrl)
            tvTrackDetailTitle.text = track.title
            tvTrackDetailArtist.text = track.artist

            btnTrackDetailSave.setOnClickListener {
                viewModel.updateComment(etTrackDetailComment.text.toString())
            }
        }

        // Int 타입의 day를 Date 타입으로 변환
        val date = convertDayToDate(day)

        // ViewModel에서 현재 날짜 설정
        viewModel.setCurrentDate(date)

        // 코멘트 관찰
        lifecycleScope.launch {
            viewModel.comment.collectLatest { comment ->
                if (comment != binding.etTrackDetailComment.text.toString()) {
                    binding.etTrackDetailComment.setText(comment)
                    binding.etTrackDetailComment.setSelection(comment.length)
                    Log.d("TrackDetailActivity", "코멘트 로드: $comment")
                }
            }
        }

        // 저장 상태 관찰
        lifecycleScope.launch {
            viewModel.saveStatus.collectLatest { status ->
                when (status) {
                    is TrackDetailViewModel.SaveStatus.Success -> {
                        Toast.makeText(this@TrackDetailActivity, "코멘트가 저장되었습니다", Toast.LENGTH_SHORT).show()
                    }
                    is TrackDetailViewModel.SaveStatus.Error -> {
                        Toast.makeText(this@TrackDetailActivity, "저장 실패: ${status.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun convertDayToDate(day: Int): Date {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        calendar.set(year, month, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
}