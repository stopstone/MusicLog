package com.stopstone.musicplaylist.domain.usecase.insta_share

import com.stopstone.musicplaylist.data.local.settings.InstagramSharePreferences
import javax.inject.Inject

class UpdateInstagramShareSettingUseCase
    @Inject
    constructor(
        private val instagramSharePreferences: InstagramSharePreferences,
    ) {
        suspend fun setShowEmotions(show: Boolean) {
            instagramSharePreferences.setShowEmotions(show)
        }

        suspend fun setShowMemo(show: Boolean) {
            instagramSharePreferences.setShowMemo(show)
        }
    }
