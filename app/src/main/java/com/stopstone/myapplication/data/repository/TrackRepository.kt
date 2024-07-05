package com.stopstone.myapplication.data.repository

import com.stopstone.myapplication.data.db.DailyTrackDao
import com.stopstone.myapplication.data.model.DailyTrack
import com.stopstone.myapplication.data.model.Track
import java.util.Date
import javax.inject.Inject

class TrackRepository @Inject constructor(
    private val dailyTrackDao: DailyTrackDao
) {
    suspend fun saveDailyTrack(track: Track, date: Date) {
        val dailyTrack = DailyTrack(track = track, date = date)
        dailyTrackDao.insert(dailyTrack)
    }

    suspend fun getTodayTrack(date: Date): DailyTrack? {
        return dailyTrackDao.getDailyTrack(date)
    }
}