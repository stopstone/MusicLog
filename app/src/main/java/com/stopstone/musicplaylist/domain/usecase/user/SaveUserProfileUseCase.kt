package com.stopstone.musicplaylist.domain.usecase.user

import com.stopstone.musicplaylist.data.model.dto.UserProfileDto
import com.stopstone.musicplaylist.domain.repository.user.UserProfileRepository
import javax.inject.Inject

class SaveUserProfileUseCase
    @Inject
    constructor(
        private val userProfileRepository: UserProfileRepository,
    ) {
        suspend operator fun invoke(
            userId: String,
            profileDto: UserProfileDto,
        ): Result<Unit> {
            return userProfileRepository.saveUserProfile(userId, profileDto)
        }
    }

