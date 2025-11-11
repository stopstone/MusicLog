package com.stopstone.musicplaylist.domain.usecase.user

import com.stopstone.musicplaylist.data.local.auth.UserPreferences
import javax.inject.Inject

class LogoutUseCase
    @Inject
    constructor(
        private val userPreferences: UserPreferences,
    ) {
        suspend operator fun invoke() {
            userPreferences.clearUserId()
        }
    }

