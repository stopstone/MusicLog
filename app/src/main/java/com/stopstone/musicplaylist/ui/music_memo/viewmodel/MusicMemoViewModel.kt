package com.stopstone.musicplaylist.ui.music_memo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.data.local.settings.EmotionPreferences
import com.stopstone.musicplaylist.domain.model.Emotions
import com.stopstone.musicplaylist.domain.usecase.search.GetEmotionsUseCase
import com.stopstone.musicplaylist.domain.usecase.search.SaveDailyTrackUseCase
import com.stopstone.musicplaylist.ui.common.resource.StringResourceProvider
import com.stopstone.musicplaylist.ui.model.TrackUiState
import com.stopstone.musicplaylist.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicMemoViewModel
    @Inject
    constructor(
        private val saveDailyTrackUseCase: SaveDailyTrackUseCase,
        private val getEmotionsUseCase: GetEmotionsUseCase,
        private val emotionPreferences: EmotionPreferences,
        private val stringResourceProvider: StringResourceProvider,
    ) : ViewModel() {
        private val _selectedEmotions = MutableStateFlow<List<String>>(emptyList())
        val selectedEmotions: StateFlow<List<String>> = _selectedEmotions.asStateFlow()

        private val _availableEmotions = MutableStateFlow<List<String>>(emptyList())
        val availableEmotions: StateFlow<List<String>> = _availableEmotions.asStateFlow()

        private val _trackSaved = MutableSharedFlow<Boolean>()
        val trackSaved: SharedFlow<Boolean> = _trackSaved.asSharedFlow()

        init {
            loadAvailableEmotions()
        }

        private fun loadAvailableEmotions() {
            viewModelScope.launch {
                combine(
                    kotlinx.coroutines.flow.flow { emit(getEmotionsUseCase()) },
                    emotionPreferences.getCustomEmotions(),
                    emotionPreferences.getEmotionOrder(),
                    emotionPreferences.getHiddenEmotions(),
                ) { baseEmotions, customEmotions, order, hiddenEmotions ->
                    buildDisplayEmotionList(baseEmotions, customEmotions, order, hiddenEmotions)
                }.collect { list ->
                    _availableEmotions.value = list
                }
            }
        }

        private fun buildDisplayEmotionList(
            baseEmotions: List<Emotions>,
            customEmotions: Set<String>,
            order: String?,
            hiddenEmotions: Set<String>,
        ): List<String> {
            val emotionIdToName: MutableMap<String, String> = mutableMapOf()
            baseEmotions.forEach { emotion ->
                val id = emotion.name
                val display = stringResourceProvider.getString(emotion.stringResId)
                emotionIdToName[id] = display
            }
            customEmotions.forEach { customName ->
                val id = "custom_$customName"
                emotionIdToName[id] = customName
            }
            val ordered: List<Pair<String, String>> =
                if (!order.isNullOrEmpty()) {
                    val orderList = order.split(",")
                    val orderedKnown = orderList.mapNotNull { id -> emotionIdToName[id]?.let { id to it } }
                    val remaining =
                        emotionIdToName
                            .filterKeys { id -> !orderList.contains(id) }
                            .map { it.key to it.value }
                    orderedKnown + remaining
                } else {
                    emotionIdToName.map { it.key to it.value }
                }
            return ordered
                .filterNot { (id, _) -> hiddenEmotions.contains(id) }
                .map { it.second }
        }

        fun toggleEmotion(emotionName: String): Boolean {
            val currentList = _selectedEmotions.value.toMutableList()
            return if (currentList.contains(emotionName)) {
                currentList.remove(emotionName)
                _selectedEmotions.value = currentList
                true
            } else if (currentList.size < MAX_SELECTED_EMOTIONS) {
                currentList.add(emotionName)
                _selectedEmotions.value = currentList
                true
            } else {
                false
            }
        }

    fun saveTrack(track: TrackUiState, comment: String?) {
        viewModelScope.launch {
            val today = DateUtils.getTodayDate()
            try {
                saveDailyTrackUseCase(track, _selectedEmotions.value, today, comment)
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
