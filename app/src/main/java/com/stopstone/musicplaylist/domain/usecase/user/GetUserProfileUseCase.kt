package com.stopstone.musicplaylist.domain.usecase.user

import com.stopstone.musicplaylist.data.model.dto.UserProfileDto
import com.stopstone.musicplaylist.domain.repository.user.UserProfileRepository
import javax.inject.Inject

class GetUserProfileUseCase
    @Inject
    constructor(
        private val userProfileRepository: UserProfileRepository,
    ) {
        suspend operator fun invoke(userId: String): Result<UserProfileDto?> {
            return userProfileRepository.getUserProfile(userId)
        }
    }

