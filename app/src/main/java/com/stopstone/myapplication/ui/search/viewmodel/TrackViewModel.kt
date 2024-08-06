package com.stopstone.myapplication.ui.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.ui.model.TrackUiState
import com.stopstone.myapplication.domain.usecase.search.SaveDailyTrackUseCase
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
    private val _trackSaved = MutableSharedFlow<Boolean>()
    val trackSaved: SharedFlow<Boolean> = _trackSaved.asSharedFlow()

    fun saveTrack(track: TrackUiState) = viewModelScope.launch {
        val today = DateUtils.getTodayDate()
        runCatching {
            saveDailyTrackUseCase(track, today)
        }.onSuccess {
            _trackSaved.emit(true)
        }.onFailure {
            _trackSaved.emit(false)
        }
    }
}