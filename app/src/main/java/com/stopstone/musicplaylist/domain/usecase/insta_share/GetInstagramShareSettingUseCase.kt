package com.stopstone.musicplaylist.domain.usecase.insta_share

import com.stopstone.musicplaylist.data.local.settings.InstagramSharePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class InstagramShareSetting(
    val showEmotions: Boolean,
    val showMemo: Boolean,
)

class GetInstagramShareSettingUseCase
    @Inject
    constructor(
        private val instagramSharePreferences: InstagramSharePreferences,
    ) {
        operator fun invoke(): Flow<InstagramShareSetting> {
            return combine(
                instagramSharePreferences.getShowEmotions(),
                instagramSharePreferences.getShowMemo(),
            ) { showEmotions, showMemo ->
                InstagramShareSetting(
                    showEmotions = showEmotions,
                    showMemo = showMemo,
                )
            }
        }
    }

