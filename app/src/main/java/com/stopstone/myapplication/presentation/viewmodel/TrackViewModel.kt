package com.stopstone.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.domain.model.SaveResult
import com.stopstone.myapplication.domain.model.TrackUiState
import com.stopstone.myapplication.domain.usecase.SaveDailyTrackUseCase
import com.stopstone.myapplication.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val saveDailyTrackUseCase: SaveDailyTrackUseCase
) : ViewModel() {
    private val _savedTrack = MutableSharedFlow<SaveResult>()
    val savedTrack: SharedFlow<SaveResult> = _savedTrack.asSharedFlow()

    fun saveTrack(track: TrackUiState) = viewModelScope.launch {
        _savedTrack.emit(
            try {
                val today = DateUtils.getTodayDate()
                saveDailyTrackUseCase(track, today)
                SaveResult.Success
            } catch (e: Exception) {
                SaveResult.Error(e.message ?: "Unknown error occurred")
            }
        )
    }
}