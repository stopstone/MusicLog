package com.stopstone.musicplaylist.domain.repository.common

import com.stopstone.musicplaylist.data.model.entity.DailyTrack
import java.util.Date

interface TrackRepository {
    suspend fun saveDailyTrack(dailyTrack: DailyTrack)
    suspend fun getTodayTrack(date: Date): DailyTrack?
    suspend fun getTracksForMonth(year: Int, month: Int): List<DailyTrack>
    suspend fun getComment(dateMillis: Date): String?
    suspend fun updateComment(dateMillis: Date, comment: String)
    suspend fun deleteTrackByDate(dateMillis: Date)
    suspend fun deleteAllTracks()
    suspend fun syncFromFirestore(userId: String): Result<Unit>
    suspend fun getMusicCount(): Int
}