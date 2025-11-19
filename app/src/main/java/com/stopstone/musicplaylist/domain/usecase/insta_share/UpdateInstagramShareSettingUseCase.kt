package com.stopstone.musicplaylist.domain.usecase.insta_share

import com.stopstone.musicplaylist.domain.repository.insta_share.InstagramShareSettingRepository
import javax.inject.Inject

class UpdateInstagramShareSettingUseCase
    @Inject
    constructor(
        private val repository: InstagramShareSettingRepository,
    ) {
        suspend fun setShowEmotions(show: Boolean) {
            repository.setShowEmotions(show)
        }

        suspend fun setShowMemo(show: Boolean) {
            repository.setShowMemo(show)
        }

        suspend fun setShowRecordedTime(show: Boolean) {
            repository.setShowRecordedTime(show)
        }
    }
