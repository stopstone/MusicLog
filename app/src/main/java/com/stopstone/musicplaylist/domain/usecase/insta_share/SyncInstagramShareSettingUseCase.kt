package com.stopstone.musicplaylist.domain.usecase.insta_share

import com.stopstone.musicplaylist.domain.repository.insta_share.InstagramShareSettingRepository
import javax.inject.Inject

class SyncInstagramShareSettingUseCase
    @Inject
    constructor(
        private val repository: InstagramShareSettingRepository,
    ) {
        suspend operator fun invoke(userId: String): Result<Unit> = repository.syncRemoteSettings(userId)
    }

