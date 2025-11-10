package com.stopstone.musicplaylist.domain.usecase.login

import com.stopstone.musicplaylist.data.local.auth.UserPreferences
import javax.inject.Inject

class SaveUserIdUseCase @Inject constructor(
    private val userPreferences: UserPreferences
) {
    suspend operator fun invoke(userId: String) {
        userPreferences.saveUserId(userId)
    }
}

