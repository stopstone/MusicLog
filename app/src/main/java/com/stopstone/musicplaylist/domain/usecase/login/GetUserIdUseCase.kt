package com.stopstone.musicplaylist.domain.usecase.login

import com.stopstone.musicplaylist.data.local.auth.UserPreferences
import javax.inject.Inject

class GetUserIdUseCase @Inject constructor(
    private val userPreferences: UserPreferences
) {
    suspend operator fun invoke(): String? {
        val userId = userPreferences.getUserIdSync()
        return userId
    }
}

