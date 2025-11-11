package com.stopstone.musicplaylist.domain.repository.insta_share

import com.stopstone.musicplaylist.domain.usecase.insta_share.InstagramShareSetting
import kotlinx.coroutines.flow.Flow

interface InstagramShareSettingRepository {
    fun getSettings(): Flow<InstagramShareSetting>

    suspend fun setShowEmotions(show: Boolean)

    suspend fun setShowMemo(show: Boolean)
}

