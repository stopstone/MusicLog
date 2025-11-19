package com.stopstone.musicplaylist.ui.detail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.domain.usecase.detail.DeleteTrackUseCase
import com.stopstone.musicplaylist.domain.usecase.detail.GetCommentUseCase
import com.stopstone.musicplaylist.domain.usecase.detail.UpdateCommentUseCase
import com.stopstone.musicplaylist.domain.usecase.home.GetTodayTrackUseCase
import com.stopstone.musicplaylist.domain.usecase.insta_share.GetInstagramShareSettingUseCase
import com.stopstone.musicplaylist.domain.usecase.insta_share.InstagramShareSetting
import com.stopstone.musicplaylist.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TrackDetailViewModel
    @Inject
    constructor(
        private val getCommentUseCase: GetCommentUseCase,
        private val updateCommentUseCase: UpdateCommentUseCase,
        private val deleteTrackUseCase: DeleteTrackUseCase,
        private val getInstagramShareSettingUseCase: GetInstagramShareSettingUseCase,
        private val getTodayTrackUseCase: GetTodayTrackUseCase,
    ) : ViewModel() {
        private val _comment = MutableStateFlow("")
        val comment: StateFlow<String> = _comment.asStateFlow()

        private val _recordedAt = MutableStateFlow<Date?>(null)
        val recordedAt: StateFlow<Date?> = _recordedAt.asStateFlow()

        private val _deleteResult = MutableSharedFlow<Boolean>()
        val deleteResult: SharedFlow<Boolean> = _deleteResult

        private lateinit var currentDate: Date

        // 정규화된 날짜를 currentDate에 저장
        fun setCurrentDate(date: Date) {
            currentDate = DateUtils.normalizeDate(date)
            loadComment() // 해당하는 날짜의 코멘트를 가져오기
            loadRecordedAt() // 저장한 시각 정보를 가져오기
        }

        // 해당하는 날짜의 코멘트를 가져오기
        private fun loadComment() =
            viewModelScope.launch {
                runCatching {
                    getCommentUseCase(currentDate)
                }.onSuccess { comment ->
                    _comment.value = comment
                    Log.d("TrackDetailViewModel", "Comment loaded successfully: $comment")
                }.onFailure { e ->
                    Log.e("TrackDetailViewModel", "Error loading comment", e)
                }
            }

        // 저장한 시각 정보를 가져오기
        private fun loadRecordedAt() =
            viewModelScope.launch {
                runCatching {
                    getTodayTrackUseCase(currentDate)
                }.onSuccess { dailyTrack ->
                    _recordedAt.value = dailyTrack?.recordedAt
                    Log.d("TrackDetailViewModel", "RecordedAt loaded: ${dailyTrack?.recordedAt}")
                }.onFailure { e ->
                    Log.e("TrackDetailViewModel", "Error loading recordedAt", e)
                    _recordedAt.value = null
                }
            }

        // 작성버튼이 눌리면 새로운 코멘트 저장
        fun updateComment(newComment: String) =
            viewModelScope.launch {
                runCatching {
                    updateCommentUseCase(currentDate, newComment)
                }.onSuccess {
                    Log.d("TrackDetailViewModel", "Comment updated successfully")
                }.onFailure { e ->
                    Log.e("TrackDetailViewModel", "Error updating comment", e)
                }
            }

        fun deleteTrack() =
            viewModelScope.launch {
                runCatching {
                    deleteTrackUseCase(currentDate)
                }.onSuccess {
                    _deleteResult.emit(true)
                    Log.d("TrackDetailViewModel", "Track deleted successfully")
                }.onFailure { e ->
                    _deleteResult.emit(false)
                    Log.e("TrackDetailViewModel", "Error deleting track", e)
                }
            }

        // 인스타그램 공유 설정 가져오기
        suspend fun getInstagramShareSettings(): InstagramShareSetting = getInstagramShareSettingUseCase().first()
    }
