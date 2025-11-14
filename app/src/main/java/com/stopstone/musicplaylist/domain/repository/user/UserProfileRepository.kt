package com.stopstone.musicplaylist.domain.repository.user

import com.stopstone.musicplaylist.data.model.dto.UserProfileDto

interface UserProfileRepository {
    suspend fun saveUserProfile(userId: String, profileDto: UserProfileDto): Result<Unit>
    suspend fun getUserProfile(userId: String): Result<UserProfileDto?>
}

