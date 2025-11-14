package com.stopstone.musicplaylist.domain.usecase.emotion_setting

import com.stopstone.musicplaylist.domain.repository.emotion_setting.EmotionSettingRepository
import javax.inject.Inject

class BackupEmotionSettingsUseCase
    @Inject
    constructor(
        private val repository: EmotionSettingRepository,
    ) {
        suspend fun execute(): Result<Unit> = repository.backupEmotionSettings()
    }
