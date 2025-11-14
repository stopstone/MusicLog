package com.stopstone.musicplaylist.domain.usecase.user

import com.stopstone.musicplaylist.data.local.dao.DailyTrackDao
import com.stopstone.musicplaylist.data.local.dao.SearchHistoryDao
import com.stopstone.musicplaylist.data.local.dao.SignatureSongDao
import com.stopstone.musicplaylist.data.remote.datasource.FirestoreDataSource
import javax.inject.Inject

class ClearUserDataUseCase
    @Inject
    constructor(
        private val firestoreDataSource: FirestoreDataSource,
        private val dailyTrackDao: DailyTrackDao,
        private val signatureSongDao: SignatureSongDao,
        private val searchHistoryDao: SearchHistoryDao,
    ) {
        suspend operator fun invoke(userId: String): Result<Unit> {
            return try {
                firestoreDataSource.deleteAllMusics(userId).getOrThrow() // 모든 음악 데이터 초기화
                firestoreDataSource.deleteAllSignatureSongs(userId).getOrThrow() // 인생곡 초기화
                firestoreDataSource.deleteInstagramShareSetting(userId).getOrThrow() // 인스타 설정 초기화
                firestoreDataSource.deleteEmotionSettings(userId).getOrThrow() // 감정 태그 초기화
                dailyTrackDao.deleteAllTracks()
                signatureSongDao.deleteAllSignatureSongs()
                searchHistoryDao.deleteAllSearches()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

