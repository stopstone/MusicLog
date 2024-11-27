package com.stopstone.musicplaylist.data.repository.common

import com.stopstone.musicplaylist.data.local.dao.DailyTrackDao
import com.stopstone.musicplaylist.data.model.entity.DailyTrack
import com.stopstone.musicplaylist.domain.repository.common.TrackRepository
import com.stopstone.musicplaylist.util.DateUtils.getMonthEnd
import com.stopstone.musicplaylist.util.DateUtils.getMonthStart
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class TrackRepositoryImpl @Inject constructor(
    private val dailyTrackDao: DailyTrackDao
) : TrackRepository {
    override suspend fun saveDailyTrack(dailyTrack: DailyTrack) {
        dailyTrackDao.upsert(dailyTrack)
    }

    override suspend fun getTodayTrack(date: Date): DailyTrack? {
        return dailyTrackDao.getDailyTrack(date)
    }

    override suspend fun getComment(date: Date): String? {
        return dailyTrackDao.getComment(date)
    }

    override suspend fun updateComment(date: Date, comment: String) {
        dailyTrackDao.updateComment(date, comment)
    }

    override suspend fun deleteTrackByDate(dateMillis: Date) {
        dailyTrackDao.deleteDailyTrack(dateMillis)
    }

    override suspend fun deleteAllTracks() {
        dailyTrackDao.deleteAllTracks()
    }

    override suspend fun getTracksForMonth(year: Int, month: Int): List<DailyTrack> {
        val calendar = Calendar.getInstance()
        val startDate = calendar.getMonthStart(year, month)
        val endDate = calendar.getMonthEnd(year, month)

        return dailyTrackDao.getTracksForDateRange(startDate, endDate)
    }
}