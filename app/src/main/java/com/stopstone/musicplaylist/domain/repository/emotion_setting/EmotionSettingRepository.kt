package com.stopstone.musicplaylist.domain.repository.emotion_setting

interface EmotionSettingRepository {
    suspend fun backupEmotionSettings(): Result<Unit>

    suspend fun syncEmotionSettings(userId: String): Result<Unit>
}
