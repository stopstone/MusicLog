package com.stopstone.musicplaylist.ui.emotion_setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.domain.usecase.search.GetEmotionsUseCase
import com.stopstone.musicplaylist.ui.emotion_setting.model.EmotionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmotionSettingViewModel
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val getEmotionsUseCase: GetEmotionsUseCase,
    ) : ViewModel() {
        private val _emotions = MutableStateFlow<List<EmotionUiState>>(emptyList())
        val emotions: StateFlow<List<EmotionUiState>> = _emotions.asStateFlow()

        init {
            loadEmotions()
        }

        // 감정 리스트 로드
        private fun loadEmotions() {
            viewModelScope.launch {
                try {
                    val emotionsList = getEmotionsUseCase()
                    _emotions.value =
                        emotionsList.map { emotion ->
                            EmotionUiState(
                                emotion = emotion,
                                displayName = emotion.getDisplayName(context),
                                isSelected = false,
                            )
                        }
                } catch (e: Exception) {
                    // 에러 처리
                    _emotions.value = emptyList()
                }
            }
        }

        // 감정 선택/해제 토글
        fun toggleEmotion(emotionUiState: EmotionUiState) {
            _emotions.value =
                _emotions.value.map { emotion ->
                    if (emotion.emotion == emotionUiState.emotion) {
                        emotion.copy(isSelected = !emotion.isSelected)
                    } else {
                        emotion
                    }
                }
        }
    }
