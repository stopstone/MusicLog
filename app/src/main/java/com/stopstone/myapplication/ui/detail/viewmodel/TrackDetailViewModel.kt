package com.stopstone.myapplication.ui.detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.domain.usecase.detail.GetCommentUseCase
import com.stopstone.myapplication.domain.usecase.detail.UpdateCommentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TrackDetailViewModel @Inject constructor(
    private val getCommentUseCase: GetCommentUseCase,
    private val updateCommentUseCase: UpdateCommentUseCase
) : ViewModel() {
    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment.asStateFlow()

    private var currentDate: Date = Date()

    fun setCurrentDate(date: Date) {
        currentDate = date
        viewModelScope.launch {
            _comment.value = getCommentUseCase(date)
        }
    }

    fun updateComment(newComment: String) {
        _comment.value = newComment
        viewModelScope.launch {
            runCatching {
                updateCommentUseCase(currentDate, newComment)
            }
        }
    }
}