package com.stopstone.musicplaylist.domain.usecase.user

import com.stopstone.musicplaylist.data.local.auth.UserPreferences
import javax.inject.Inject

class LogoutUseCase
    @Inject
    constructor(
        private val userPreferences: UserPreferences,
        private val resetLocalUserCacheUseCase: ResetLocalUserCacheUseCase,
    ) {
        suspend operator fun invoke() {
            // 로그아웃 시 로컬 캐시를 정리해 계정 간 데이터 섞임을 방지
            resetLocalUserCacheUseCase()
            userPreferences.clearUserId()
        }
    }

