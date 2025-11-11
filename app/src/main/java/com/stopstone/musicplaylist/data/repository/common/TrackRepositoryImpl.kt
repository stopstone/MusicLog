package com.stopstone.musicplaylist.data.repository.common

import android.util.Log
import com.stopstone.musicplaylist.data.local.auth.UserPreferences
import com.stopstone.musicplaylist.data.local.dao.DailyTrackDao
import com.stopstone.musicplaylist.data.model.dto.MusicDto
import com.stopstone.musicplaylist.data.model.entity.DailyTrack
import com.stopstone.musicplaylist.data.remote.datasource.FirestoreDataSource
import com.stopstone.musicplaylist.domain.repository.common.TrackRepository
import com.stopstone.musicplaylist.util.DateUtils.getMonthEnd
import com.stopstone.musicplaylist.util.DateUtils.getMonthStart
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class TrackRepositoryImpl @Inject constructor(
    private val dailyTrackDao: DailyTrackDao,
    private val firestoreDataSource: FirestoreDataSource,
    private val userPreferences: UserPreferences
) : TrackRepository {

    /**
     * DailyTrack 저장 (로컬 DB + Firestore 백업)
     */
    override suspend fun saveDailyTrack(dailyTrack: DailyTrack) {
        // 로컬 DB에 저장
        dailyTrackDao.upsert(dailyTrack)
        
        // Firestore에 백업
        val userId = userPreferences.getUserIdSync()
        if (userId != null) {
            val musicDto = MusicDto.fromDailyTrack(dailyTrack)
            firestoreDataSource.saveMusic(userId, musicDto)
        }
    }

    override suspend fun getTodayTrack(date: Date): DailyTrack? {
        return dailyTrackDao.getDailyTrack(date)
    }

    override suspend fun getComment(date: Date): String? {
        return dailyTrackDao.getComment(date)
    }

    /**
     * 코멘트 업데이트 (로컬 DB + Firestore 백업)
     */
    override suspend fun updateComment(date: Date, comment: String) {
        // 로컬 DB 업데이트
        dailyTrackDao.updateComment(date, comment)
        
        // Firestore 백업
        val userId = userPreferences.getUserIdSync()
        if (userId != null) {
            val musicId = date.time.toString()
            firestoreDataSource.updateComment(userId, musicId, comment)
        }
    }

    /**
     * 날짜로 트랙 삭제 (로컬 DB + Firestore)
     */
    override suspend fun deleteTrackByDate(dateMillis: Date) {
        // 로컬 DB 삭제
        dailyTrackDao.deleteDailyTrack(dateMillis)
        
        // Firestore 삭제
        val userId = userPreferences.getUserIdSync()
        if (userId != null) {
            val musicId = dateMillis.time.toString()
            firestoreDataSource.deleteMusic(userId, musicId)
        }
    }

    /**
     * 모든 트랙 삭제 (로컬 DB + Firestore)
     */
    override suspend fun deleteAllTracks() {
        // 로컬 DB 삭제
        dailyTrackDao.deleteAllTracks()
        
        // Firestore 삭제
        val userId = userPreferences.getUserIdSync()
        if (userId != null) {
            firestoreDataSource.deleteAllMusics(userId)
        }
    }

    override suspend fun getTracksForMonth(year: Int, month: Int): List<DailyTrack> {
        val calendar = Calendar.getInstance()
        val startDate = calendar.getMonthStart(year, month)
        val endDate = calendar.getMonthEnd(year, month)

        return dailyTrackDao.getTracksForDateRange(startDate, endDate)
    }

    /**
     * Firestore와 양방향 동기화
     * - 로그인 시 호출
     * 1. 로컬 DB → Firestore: 로컬에 있고 Firestore에 없는 데이터 업로드
     * 2. Firestore → 로컬 DB: Firestore에 있고 로컬에 없는 데이터 다운로드
     */
    override suspend fun syncFromFirestore(userId: String): Result<Unit> {
        return try {
            // 1. 로컬 DB에서 모든 데이터 가져오기
            val localTracks = dailyTrackDao.getAllTracks()
            
            // 2. Firestore에서 모든 데이터 가져오기
            val firestoreResult = firestoreDataSource.loadAllMusics(userId)
            val firestoreTracks = firestoreResult.getOrNull() ?: emptyList()
            
            // 3. 비교를 위해 Map으로 변환 (날짜를 키로 사용)
            val firestoreMap = firestoreTracks.associateBy { it.date.time }
            val localMap = localTracks.associateBy { it.date.time }
            
            // 4. 로컬 → Firestore (로컬에 있고 Firestore에 없는 데이터)
            localTracks.forEach { localTrack ->
                val dateKey = localTrack.date.time
                if (!firestoreMap.containsKey(dateKey)) {
                    val musicDto = MusicDto.fromDailyTrack(localTrack)
                    firestoreDataSource.saveMusic(userId, musicDto)
                }
            }
            
            // 5. Firestore → 로컬 (Firestore 데이터를 모두 로컬에 저장/업데이트)
            firestoreTracks.forEach { musicDto ->
                val dailyTrack = musicDto.toDailyTrack()
                dailyTrackDao.upsert(dailyTrack)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMusicCount(): Int {
        return dailyTrackDao.getMusicCount()
    }
}