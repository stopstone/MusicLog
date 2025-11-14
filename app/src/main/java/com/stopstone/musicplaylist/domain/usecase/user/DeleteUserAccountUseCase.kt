package com.stopstone.musicplaylist.domain.usecase.user

import com.stopstone.musicplaylist.data.local.auth.UserPreferences
import com.stopstone.musicplaylist.data.local.dao.DailyTrackDao
import com.stopstone.musicplaylist.data.local.dao.SearchHistoryDao
import com.stopstone.musicplaylist.data.local.dao.SignatureSongDao
import com.stopstone.musicplaylist.data.remote.datasource.FirestoreDataSource
import javax.inject.Inject

class DeleteUserAccountUseCase
    @Inject
    constructor(
        private val firestoreDataSource: FirestoreDataSource,
        private val userPreferences: UserPreferences,
        private val dailyTrackDao: DailyTrackDao,
        private val signatureSongDao: SignatureSongDao,
        private val searchHistoryDao: SearchHistoryDao,
    ) {
        suspend operator fun invoke(userId: String): Result<Unit> {
            return try {
                firestoreDataSource.deleteUserAccount(userId).getOrThrow() // 원격 사용자 데이터 삭제
                dailyTrackDao.deleteAllTracks()
                signatureSongDao.deleteAllSignatureSongs()
                searchHistoryDao.deleteAllSearches()
                userPreferences.clearUserId()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

