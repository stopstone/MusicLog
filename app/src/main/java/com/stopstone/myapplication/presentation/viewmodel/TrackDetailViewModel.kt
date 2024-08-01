package com.stopstone.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.domain.usecase.GetCommentUseCase
import com.stopstone.myapplication.domain.usecase.UpdateCommentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _saveStatus = MutableSharedFlow<SaveStatus>()
    val saveStatus: SharedFlow<SaveStatus> = _saveStatus.asSharedFlow()

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
            try {
                updateCommentUseCase(currentDate, newComment)
                _saveStatus.emit(SaveStatus.Success)
            } catch (e: Exception) {
                _saveStatus.emit(SaveStatus.Error(e.message ?: "Unknown error"))
            }
        }
    }

    sealed class SaveStatus {
        object Success : SaveStatus()
        data class Error(val message: String) : SaveStatus()
    }
}