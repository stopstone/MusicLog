package com.stopstone.musicplaylist.ui.music_memo.viewmodel

import androidx.lifecycle.ViewModel
import com.stopstone.musicplaylist.domain.model.Emotions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MusicMemoViewModel
    @Inject
    constructor() : ViewModel() {
        private val _emotions = MutableStateFlow(Emotions.entries.toList())
        val emotions: StateFlow<List<Emotions>> = _emotions.asStateFlow()

        private val _selectedEmotions = MutableStateFlow<List<Emotions>>(emptyList())
        val selectedEmotions: StateFlow<List<Emotions>> = _selectedEmotions.asStateFlow()

        fun toggleEmotion(emotion: Emotions): Boolean {
            val currentList = _selectedEmotions.value.toMutableList()
            if (currentList.contains(emotion)) {
                currentList.remove(emotion)
                _selectedEmotions.value = currentList
                return true
            } else {
                if (currentList.size < MAX_SELECTED_EMOTIONS) {
                    currentList.add(emotion)
                    _selectedEmotions.value = currentList
                    return true
                } else {
                    return false
                }
            }
        }

        companion object {
            const val MAX_SELECTED_EMOTIONS: Int = 5
        }
    }
