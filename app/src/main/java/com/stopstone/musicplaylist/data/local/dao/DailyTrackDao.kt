package com.stopstone.musicplaylist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.stopstone.musicplaylist.data.model.entity.DailyTrack
import java.util.Date

@Dao
interface DailyTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyTrack: DailyTrack)

    @Query("SELECT * FROM daily_tracks WHERE date = :date")
    suspend fun getDailyTrack(date: Date): DailyTrack?

    @Transaction
    suspend fun upsert(dailyTrack: DailyTrack) {
        val existingTrack = getDailyTrack(dailyTrack.date)
        if (existingTrack == null) {
            insert(dailyTrack)
        } else {
            insert(dailyTrack.copy(id = existingTrack.id))
        }
    }

    @Query("SELECT * FROM daily_tracks WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTracksForDateRange(startDate: Date, endDate: Date): List<DailyTrack>

    @Query("SELECT * FROM daily_tracks ORDER BY date DESC")
    suspend fun getAllTracks(): List<DailyTrack>

    @Query("UPDATE daily_tracks SET comment = :comment WHERE date = :date")
    suspend fun updateComment(date: Date, comment: String)

    @Query("SELECT comment FROM daily_tracks WHERE date = :date")
    suspend fun getComment(date: Date): String?

    @Query("DELETE FROM daily_tracks WHERE date = :date")
    suspend fun deleteDailyTrack(date: Date)

    @Query("DELETE FROM daily_tracks")
    suspend fun deleteAllTracks()
}