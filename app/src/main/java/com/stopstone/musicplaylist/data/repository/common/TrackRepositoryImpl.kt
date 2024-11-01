package com.stopstone.musicplaylist.data.repository.common

import com.stopstone.musicplaylist.data.local.dao.DailyTrackDao
import com.stopstone.musicplaylist.data.model.entity.DailyTrack
import com.stopstone.musicplaylist.domain.repository.common.TrackRepository
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
        val startDate = Calendar.getInstance().apply {
            set(year, month - 1, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val endDate = Calendar.getInstance().apply {
            set(year, month - 1, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time

        return dailyTrackDao.getTracksForDateRange(startDate, endDate)
    }
}