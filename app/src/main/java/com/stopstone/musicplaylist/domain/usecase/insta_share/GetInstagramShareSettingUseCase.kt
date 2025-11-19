package com.stopstone.musicplaylist.domain.usecase.insta_share

import com.stopstone.musicplaylist.domain.repository.insta_share.InstagramShareSettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class InstagramShareSetting(
    val showEmotions: Boolean,
    val showMemo: Boolean,
    val showRecordedTime: Boolean,
)

class GetInstagramShareSettingUseCase
    @Inject
    constructor(
        private val repository: InstagramShareSettingRepository,
    ) {
        operator fun invoke(): Flow<InstagramShareSetting> = repository.getSettings()
    }

