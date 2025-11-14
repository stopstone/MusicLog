package com.stopstone.musicplaylist.data.repository.emotion_setting

import com.stopstone.musicplaylist.data.local.auth.UserPreferences
import com.stopstone.musicplaylist.data.local.settings.EmotionPreferences
import com.stopstone.musicplaylist.data.local.settings.model.EmotionSettingPreferences
import com.stopstone.musicplaylist.data.remote.datasource.FirestoreDataSource
import com.stopstone.musicplaylist.data.remote.dto.EmotionSettingDto
import com.stopstone.musicplaylist.domain.repository.emotion_setting.EmotionSettingRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmotionSettingRepositoryImpl
    @Inject
    constructor(
        private val emotionPreferences: EmotionPreferences,
        private val firestoreDataSource: FirestoreDataSource,
        private val userPreferences: UserPreferences,
    ) : EmotionSettingRepository {
        override suspend fun backupEmotionSettings(): Result<Unit> {
            val userId = userPreferences.getUserId().first()
            if (userId.isNullOrBlank()) {
                return Result.failure(IllegalStateException("User id is empty"))
            }
            val snapshot = emotionPreferences.getEmotionSettingsSnapshot()
            val dto =
                EmotionSettingDto(
                    customEmotionNames = snapshot.customEmotions.toList(),
                    orderedEmotionIds = snapshot.emotionOrder,
                    hiddenEmotionIds = snapshot.hiddenEmotions.toList(),
                )
            return firestoreDataSource.saveEmotionSettings(userId, dto)
        }

        override suspend fun syncEmotionSettings(userId: String): Result<Unit> {
            val remoteResult = firestoreDataSource.getEmotionSettings(userId)
            return remoteResult.fold(
                onSuccess = { dto ->
                    if (dto != null) {
                        val preferencesSnapshot =
                            EmotionSettingPreferences(
                                customEmotions = dto.customEmotionNames.toSet(),
                                emotionOrder = dto.orderedEmotionIds,
                                hiddenEmotions = dto.hiddenEmotionIds.toSet(),
                            )
                        emotionPreferences.overwriteEmotionSettings(preferencesSnapshot)
                    }
                    Result.success(Unit)
                },
                onFailure = { throwable ->
                    Result.failure(throwable)
                },
            )
        }
    }
