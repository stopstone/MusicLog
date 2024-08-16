package com.stopstone.myapplication.ui.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.data.model.Emotions
import com.stopstone.myapplication.ui.model.TrackUiState
import com.stopstone.myapplication.domain.usecase.search.SaveDailyTrackUseCase
import com.stopstone.myapplication.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val saveDailyTrackUseCase: SaveDailyTrackUseCase
) : ViewModel() {
    private val _trackSaved = MutableSharedFlow<Boolean>()
    val trackSaved: SharedFlow<Boolean> = _trackSaved.asSharedFlow()

    private val _emotions = MutableStateFlow(Emotions.entries)
    val emotions: StateFlow<List<Emotions>> = _emotions.asStateFlow()

    private val _selectedEmotions = MutableStateFlow<List<Emotions>>(emptyList())
    val selectedEmotions: StateFlow<List<Emotions>> = _selectedEmotions.asStateFlow()

    fun toggleEmotion(emotion: Emotions) {
        val currentList = _selectedEmotions.value.toMutableList()
        when (currentList.contains(emotion)) {
            true -> currentList.remove(emotion)
            false -> currentList.add(emotion)
        }
        _selectedEmotions.value = currentList
    }


    fun saveTrack(track: TrackUiState) = viewModelScope.launch {
        val today = DateUtils.getTodayDate()
        runCatching {
            saveDailyTrackUseCase(track, _selectedEmotions.value, today)
        }.onSuccess {
            _trackSaved.emit(true)
        }.onFailure {
            _trackSaved.emit(false)
        }
    }
}