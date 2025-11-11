package com.stopstone.musicplaylist.domain.usecase.user

import com.stopstone.musicplaylist.data.local.auth.UserPreferences
import com.stopstone.musicplaylist.data.local.dao.DailyTrackDao
import com.stopstone.musicplaylist.data.remote.datasource.FirestoreDataSource
import javax.inject.Inject

class DeleteUserAccountUseCase
    @Inject
    constructor(
        private val firestoreDataSource: FirestoreDataSource,
        private val userPreferences: UserPreferences,
        private val dailyTrackDao: DailyTrackDao,
    ) {
        suspend operator fun invoke(userId: String): Result<Unit> {
            return try {
                firestoreDataSource.deleteUserAccount(userId)
                dailyTrackDao.deleteAllTracks()
                userPreferences.clearUserId()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

