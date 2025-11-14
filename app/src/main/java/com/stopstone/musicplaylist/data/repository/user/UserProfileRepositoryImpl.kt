package com.stopstone.musicplaylist.data.repository.user

import com.stopstone.musicplaylist.data.model.dto.UserProfileDto
import com.stopstone.musicplaylist.data.remote.datasource.FirestoreDataSource
import com.stopstone.musicplaylist.domain.repository.user.UserProfileRepository
import javax.inject.Inject

class UserProfileRepositoryImpl
    @Inject
    constructor(
        private val firestoreDataSource: FirestoreDataSource,
    ) : UserProfileRepository {
        override suspend fun saveUserProfile(
            userId: String,
            profileDto: UserProfileDto,
        ): Result<Unit> {
            return firestoreDataSource.saveUserProfile(userId, profileDto)
        }

        override suspend fun getUserProfile(userId: String): Result<UserProfileDto?> {
            return firestoreDataSource.getUserProfile(userId)
        }
    }

