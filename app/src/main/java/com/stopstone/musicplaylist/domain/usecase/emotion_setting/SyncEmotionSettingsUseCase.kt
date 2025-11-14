package com.stopstone.musicplaylist.domain.usecase.emotion_setting

import com.stopstone.musicplaylist.domain.repository.emotion_setting.EmotionSettingRepository
import javax.inject.Inject

class SyncEmotionSettingsUseCase
    @Inject
    constructor(
        private val repository: EmotionSettingRepository,
    ) {
        suspend fun execute(userId: String): Result<Unit> = repository.syncEmotionSettings(userId)
    }
