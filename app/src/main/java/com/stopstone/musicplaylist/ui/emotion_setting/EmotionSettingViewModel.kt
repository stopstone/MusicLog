package com.stopstone.musicplaylist.ui.emotion_setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.data.local.settings.EmotionPreferences
import com.stopstone.musicplaylist.domain.model.Emotions
import com.stopstone.musicplaylist.domain.usecase.emotion_setting.BackupEmotionSettingsUseCase
import com.stopstone.musicplaylist.domain.usecase.search.GetEmotionsUseCase
import com.stopstone.musicplaylist.ui.common.resource.StringResourceProvider
import com.stopstone.musicplaylist.ui.emotion_setting.model.EmotionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmotionSettingViewModel
    @Inject
    constructor(
        private val getEmotionsUseCase: GetEmotionsUseCase,
        private val emotionPreferences: EmotionPreferences,
        private val backupEmotionSettingsUseCase: BackupEmotionSettingsUseCase,
        private val stringResourceProvider: StringResourceProvider,
    ) : ViewModel() {
        private val _emotions = MutableStateFlow<List<EmotionUiState>>(emptyList())
        val emotions: StateFlow<List<EmotionUiState>> = _emotions.asStateFlow()

        private val _isDeleteMode = MutableStateFlow(false)
        val isDeleteMode: StateFlow<Boolean> = _isDeleteMode.asStateFlow()

        private val _selectedCount = MutableStateFlow(0)
        val selectedCount: StateFlow<Int> = _selectedCount.asStateFlow()

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

        private fun getEmotionsFlow() =
            flow {
                emit(getEmotionsUseCase())
            }

        private fun buildEmotionList(
            baseEmotions: List<Emotions>,
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
                        displayName = stringResourceProvider.getString(emotion.stringResId),
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

            // 숨김 처리된 감정 필터링하고 삭제 선택 상태는 현재 값 유지
            val currentSelectedIds =
                _emotions.value
                    .filter { it.isSelectedForDelete }
                    .map { it.emotionId }
                    .toSet()

            return orderedList
                .filterNot { it.isHidden }
                .map { emotion ->
                    if (currentSelectedIds.contains(emotion.emotionId)) {
                        emotion.copy(isSelectedForDelete = true)
                    } else {
                        emotion
                    }
                }
        }

        // 커스텀 감정 추가
        fun addCustomEmotion(name: String): Boolean {
            if (name.isBlank()) return false

            // 이미 존재하는지 확인
            if (_emotions.value.any { it.displayName == name }) {
                return false
            }

            viewModelScope.launch {
                emotionPreferences.addCustomEmotion(name)
                backupEmotionSettings()
            }
            return true
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
                backupEmotionSettings()
            }
        }

        // 삭제 모드 활성화
        fun enableDeleteMode() {
            _isDeleteMode.value = true
        }

        // 삭제 모드 비활성화
        fun disableDeleteMode() {
            _isDeleteMode.value = false
            _selectedCount.value = 0
        }

        // 감정 선택 토글
        fun toggleEmotionSelection(emotion: EmotionUiState) {
            _emotions.value =
                _emotions.value.map {
                    if (it.emotionId == emotion.emotionId) {
                        it.copy(isSelectedForDelete = !it.isSelectedForDelete)
                    } else {
                        it
                    }
                }
            updateSelectedCount()
        }

        // 선택된 개수 업데이트
        private fun updateSelectedCount() {
            _selectedCount.value = _emotions.value.count { it.isSelectedForDelete }
        }

        // 선택된 감정들 삭제
        fun deleteSelectedEmotions() {
            viewModelScope.launch {
                _emotions.value
                    .filter { it.isSelectedForDelete }
                    .forEach { emotion ->
                        if (emotion.isCustom) {
                            emotionPreferences.deleteCustomEmotion(emotion.displayName)
                        } else {
                            emotionPreferences.hideEmotion(emotion.emotionId)
                        }
                    }
                disableDeleteMode()
                backupEmotionSettings()
            }
        }

        private suspend fun backupEmotionSettings() {
            backupEmotionSettingsUseCase.execute()
        }
    }
