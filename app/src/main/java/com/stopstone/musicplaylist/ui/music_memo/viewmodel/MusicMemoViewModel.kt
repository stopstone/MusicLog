package com.stopstone.musicplaylist.ui.music_memo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.domain.model.Emotions
import com.stopstone.musicplaylist.domain.usecase.search.SaveDailyTrackUseCase
import com.stopstone.musicplaylist.ui.model.TrackUiState
import com.stopstone.musicplaylist.util.DateUtils
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
class MusicMemoViewModel
    @Inject
    constructor(
        private val saveDailyTrackUseCase: SaveDailyTrackUseCase,
    ) : ViewModel() {
        private val _selectedEmotions = MutableStateFlow<List<Emotions>>(emptyList())
        val selectedEmotions: StateFlow<List<Emotions>> = _selectedEmotions.asStateFlow()

        private val _trackSaved = MutableSharedFlow<Boolean>()
        val trackSaved: SharedFlow<Boolean> = _trackSaved.asSharedFlow()

        fun toggleEmotion(emotion: Emotions): Boolean {
            val currentList = _selectedEmotions.value.toMutableList()
            return if (currentList.contains(emotion)) {
                currentList.remove(emotion)
                _selectedEmotions.value = currentList
                true
            } else if (currentList.size < MAX_SELECTED_EMOTIONS) {
                currentList.add(emotion)
                _selectedEmotions.value = currentList
                true
            } else {
                false
            }
        }

        fun saveTrack(track: TrackUiState) {
            viewModelScope.launch {
                val today = DateUtils.getTodayDate()
                try {
                    saveDailyTrackUseCase(track, _selectedEmotions.value, today)
                    _trackSaved.emit(true)
                } catch (exception: Exception) {
                    _trackSaved.emit(false)
                }
            }
        }

        companion object {
            const val MAX_SELECTED_EMOTIONS: Int = 5
        }
    }
