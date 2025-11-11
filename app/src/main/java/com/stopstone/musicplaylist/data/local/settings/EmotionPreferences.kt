package com.stopstone.musicplaylist.data.local.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// 감정 설정을 저장하는 DataStore
@Singleton
class EmotionPreferences
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        // 커스텀 감정 리스트 (JSON 형식)
        private val customEmotionsKey = stringSetPreferencesKey("custom_emotions")
        
        // 감정 순서 (Emotion ID를 쉼표로 구분)
        private val emotionOrderKey = stringPreferencesKey("emotion_order")
        
        // 숨김 처리된 감정 ID 리스트
        private val hiddenEmotionsKey = stringSetPreferencesKey("hidden_emotions")

        // 커스텀 감정 가져오기
        fun getCustomEmotions(): Flow<Set<String>> =
            dataStore.data.map { preferences ->
                preferences[customEmotionsKey] ?: emptySet()
            }

        // 커스텀 감정 추가
        suspend fun addCustomEmotion(emotionName: String) {
            dataStore.edit { preferences ->
                val current = preferences[customEmotionsKey] ?: emptySet()
                preferences[customEmotionsKey] = current + emotionName
            }
        }

        // 커스텀 감정 삭제
        suspend fun deleteCustomEmotion(emotionName: String) {
            dataStore.edit { preferences ->
                val current = preferences[customEmotionsKey] ?: emptySet()
                preferences[customEmotionsKey] = current - emotionName
            }
        }

        // 감정 순서 가져오기
        fun getEmotionOrder(): Flow<String?> =
            dataStore.data.map { preferences ->
                preferences[emotionOrderKey]
            }

        // 감정 순서 저장
        suspend fun saveEmotionOrder(order: String) {
            dataStore.edit { preferences ->
                preferences[emotionOrderKey] = order
            }
        }

        // 숨김 처리된 감정 가져오기
        fun getHiddenEmotions(): Flow<Set<String>> =
            dataStore.data.map { preferences ->
                preferences[hiddenEmotionsKey] ?: emptySet()
            }

        // 감정 숨기기
        suspend fun hideEmotion(emotionId: String) {
            dataStore.edit { preferences ->
                val current = preferences[hiddenEmotionsKey] ?: emptySet()
                preferences[hiddenEmotionsKey] = current + emotionId
            }
        }

        // 감정 보이기
        suspend fun showEmotion(emotionId: String) {
            dataStore.edit { preferences ->
                val current = preferences[hiddenEmotionsKey] ?: emptySet()
                preferences[hiddenEmotionsKey] = current - emotionId
            }
        }

        // 모든 설정 초기화
        suspend fun clearAll() {
            dataStore.edit { preferences ->
                preferences.remove(customEmotionsKey)
                preferences.remove(emotionOrderKey)
                preferences.remove(hiddenEmotionsKey)
            }
        }
    }

