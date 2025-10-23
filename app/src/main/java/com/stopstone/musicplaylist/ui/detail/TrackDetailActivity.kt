package com.stopstone.musicplaylist.ui.detail

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.ActivityTrackDetailBinding
import com.stopstone.musicplaylist.domain.model.Emotions
import com.stopstone.musicplaylist.ui.detail.viewmodel.TrackDetailViewModel
import com.stopstone.musicplaylist.util.DateUtils
import com.stopstone.musicplaylist.util.hideKeyboard
import com.stopstone.musicplaylist.util.loadImage
import com.stopstone.musicplaylist.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrackDetailActivity : AppCompatActivity() {
    private val binding: ActivityTrackDetailBinding by lazy {
        ActivityTrackDetailBinding.inflate(
            layoutInflater
        )
    }
    private val args: TrackDetailActivityArgs by navArgs()
    private val viewModel: TrackDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setLayout()
        setListeners()
        collectViewModel()
    }

    private fun setLayout() {
        Log.d("TrackDetailActivity", args.DailyTrack.toString())
        with(args.DailyTrack) {
            DateUtils.createDate(year, month, id) // 저장된 트랙의 날짜를, date 타입의 시간으로 변경
        }.also { viewModel.setCurrentDate(it) }

        with(binding) {
            val track = args.DailyTrack.track!!
            ivTrackDetailAlbumCover.loadImage(track.imageUrl)
            tvTrackDetailTitle.text = track.title
            tvTrackDetailArtist.text = track.artist
            addEmotionTags()
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
                setResult(RESULT_OK)  // 삭제 성공 결과 전달
                showToast(getString(R.string.label_track_delete))
                finish()
            } else {
                showToast(getString(R.string.label_track_delete_failed))
            }
        }
    }

    private fun setListeners() {
        binding.btnTrackDetailSave.setOnClickListener {
            viewModel.updateComment(binding.etTrackDetailComment.text.toString())
            it.hideKeyboard()
        }

        binding.btnTrackDetailDelete.setOnClickListener {
            setDialogBuilder()
        }
        binding.root.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                v.hideKeyboard()
            }
            true
        }

        binding.toolbarTrackDetail.setOnClickListener {
            finish()
        }
    }

    private fun setDialogBuilder() {
        MaterialAlertDialogBuilder(this).apply {
            setTitle(getString(R.string.delete_track_title))
            setMessage(getString(R.string.delete_track_message))
            setNegativeButton(R.string.label_cancel, null)
            setPositiveButton(R.string.description_track_delete_button) { dialogInterface: DialogInterface, i: Int ->
                viewModel.deleteTrack()
            }
            show()
        }
    }

    private fun addEmotionTags() {
        args.DailyTrack.emotions.forEach { emotion ->
            val textView = createEmotionTextView(emotion)
            addTextViewToLayout(textView)
        }
    }

    private fun createEmotionTextView(emotion: Emotions) = TextView(this).apply {
        id = View.generateViewId()
        text = emotion.getDisplayName(this@TrackDetailActivity)
        background = AppCompatResources.getDrawable(context, R.drawable.background_gray)
        setPadding(16, 8, 16, 8)
    }

    private fun addTextViewToLayout(textView: TextView) {
        binding.root.addView(textView, ConstraintLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT))
        binding.flowEmotion.addView(textView)
    }

}