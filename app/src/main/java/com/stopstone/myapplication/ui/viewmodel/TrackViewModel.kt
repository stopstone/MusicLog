package com.stopstone.myapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.data.model.SaveResult
import com.stopstone.myapplication.data.model.TrackUiState
import com.stopstone.myapplication.data.repository.TrackRepository
import com.stopstone.myapplication.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val repository: TrackRepository,
) : ViewModel() {
    private val _savedTrack = MutableLiveData<SaveResult>()
    val savedTrack: LiveData<SaveResult> = _savedTrack

    fun saveTrack(track: TrackUiState) = viewModelScope.launch {
        _savedTrack.value = try {
            val today = DateUtils.getTodayDate()
            repository.saveDailyTrack(track, today)
            SaveResult.Success
        } catch (e: Exception) {
            SaveResult.Error(e.message ?: "Unknown error occurred")
        }
    }
}