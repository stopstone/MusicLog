package com.stopstone.myapplication.ui.detail.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.domain.usecase.detail.GetCommentUseCase
import com.stopstone.myapplication.domain.usecase.detail.UpdateCommentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TrackDetailViewModel @Inject constructor(
    private val getCommentUseCase: GetCommentUseCase,
    private val updateCommentUseCase: UpdateCommentUseCase,
) : ViewModel() {
    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment.asStateFlow()

    private lateinit var currentDate: Date

    // ViewModel에서 normalizeDate()를 통해 시간 정보 초기화
    // 정규화된 날짜를 currentDate에 저장
    fun setCurrentDate(date: Date) {
        currentDate = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        loadComment() // 해당하는 날짜의 코멘트를 가져오기
    }

    // 해당하는 날짜의 코멘트를 가져오기
    private fun loadComment() = viewModelScope.launch {
        runCatching {
            getCommentUseCase(currentDate)
        }.onSuccess { comment ->
            _comment.value = comment
            Log.d("TrackDetailViewModel", "Comment loaded successfully: $comment")
        }.onFailure { e ->
            Log.e("TrackDetailViewModel", "Error loading comment", e)
        }
    }

    // 작성버튼이 눌리면 새로운 코멘트 저장
    fun updateComment(newComment: String) = viewModelScope.launch {
        runCatching {
            updateCommentUseCase(currentDate, newComment)
        }.onSuccess {
            Log.d("TrackDetailViewModel", "Comment updated successfully")
        }.onFailure { e ->
            Log.e("TrackDetailViewModel", "Error updating comment", e)
        }
    }
}
