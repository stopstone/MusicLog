package com.stopstone.musicplaylist.ui.emotion_setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.data.local.settings.EmotionPreferences
import com.stopstone.musicplaylist.domain.usecase.search.GetEmotionsUseCase
import com.stopstone.musicplaylist.ui.emotion_setting.model.EmotionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmotionSettingViewModel
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val getEmotionsUseCase: GetEmotionsUseCase,
        private val emotionPreferences: EmotionPreferences,
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
                    combine(
                        getEmotionsFlow(),
                        emotionPreferences.getCustomEmotions(),
                        emotionPreferences.getEmotionOrder(),
                        emotionPreferences.getHiddenEmotions(),
                    ) { baseEmotions, customEmotions, order, hiddenEmotions ->
                        buildEmotionList(baseEmotions, customEmotions, order, hiddenEmotions)
                    }.collect { emotionList ->
                        _emotions.value = emotionList
                    }
                } catch (e: Exception) {
                    _emotions.value = emptyList()
                }
            }
        }

        private suspend fun getEmotionsFlow() =
            kotlinx.coroutines.flow.flow {
                emit(getEmotionsUseCase())
            }

        private fun buildEmotionList(
            baseEmotions: List<com.stopstone.musicplaylist.domain.model.Emotions>,
            customEmotions: Set<String>,
            order: String?,
            hiddenEmotions: Set<String>,
        ): List<EmotionUiState> {
            val emotionMap = mutableMapOf<String, EmotionUiState>()

            // 기본 감정 추가
            baseEmotions.forEach { emotion ->
                val id = emotion.name
                emotionMap[id] =
                    EmotionUiState(
                        emotionId = id,
                        displayName = emotion.getDisplayName(context),
                        isCustom = false,
                        isHidden = hiddenEmotions.contains(id),
                        emotion = emotion,
                    )
            }

            // 커스텀 감정 추가
            customEmotions.forEach { customName ->
                val id = "custom_$customName"
                emotionMap[id] =
                    EmotionUiState(
                        emotionId = id,
                        displayName = customName,
                        isCustom = true,
                        isHidden = false,
                    )
            }

            // 순서대로 정렬
            val orderedList =
                if (order != null && order.isNotEmpty()) {
                    val orderList = order.split(",")
                    orderList
                        .mapNotNull { emotionMap[it] }
                        .plus(emotionMap.values.filter { !orderList.contains(it.emotionId) })
                } else {
                    emotionMap.values.toList()
                }

            // 숨김 처리된 감정 필터링
            return orderedList.filterNot { it.isHidden }
        }

        // 순서 변경
        fun moveEmotion(
            fromPosition: Int,
            toPosition: Int,
        ) {
            val list = _emotions.value.toMutableList()
            val item = list.removeAt(fromPosition)
            list.add(toPosition, item)
            _emotions.value = list
        }

        // 순서 저장
        fun saveEmotionOrder() {
            viewModelScope.launch {
                val order = _emotions.value.joinToString(",") { it.emotionId }
                emotionPreferences.saveEmotionOrder(order)
            }
        }
    }
