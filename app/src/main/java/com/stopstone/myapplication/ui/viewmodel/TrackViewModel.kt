package com.stopstone.myapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.data.model.Track
import com.stopstone.myapplication.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val repository: TrackRepository,
) : ViewModel() {
    private val _savedTrack = MutableLiveData<SaveResult>()
    val savedTrack: LiveData<SaveResult> = _savedTrack

    fun saveTrack(track: Track) = viewModelScope.launch {
        _savedTrack.value = try {
            val today = getTodayDate()
            repository.saveDailyTrack(track, today)
            SaveResult.Success
        } catch (e: Exception) {
            SaveResult.Error(e.message ?: "Unknown error occurred")
        }
    }

    private fun getTodayDate(): Date {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return simpleDateFormat.parse(simpleDateFormat.format(Date())) ?: Date()
    }
}

sealed class SaveResult {
    data object Success : SaveResult()
    data class Error(val message: String) : SaveResult()
}