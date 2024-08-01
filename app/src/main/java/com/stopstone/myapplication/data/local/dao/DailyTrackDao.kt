package com.stopstone.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stopstone.myapplication.data.model.entity.DailyTrack
import java.util.Date

@Dao
interface DailyTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyTrack: DailyTrack)

    @Query("SELECT * FROM daily_tracks WHERE date = :date")
    suspend fun getDailyTrack(date: Date): DailyTrack?

    @Query("SELECT * FROM daily_tracks WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTracksForDateRange(startDate: Date, endDate: Date): List<DailyTrack>

    @Query("SELECT * FROM daily_tracks ORDER BY date DESC")
    suspend fun getAllTracks(): List<DailyTrack>

    @Query("UPDATE daily_tracks SET comment = :comment WHERE date = :date")
    suspend fun updateComment(date: Date, comment: String)

    @Query("SELECT comment FROM daily_tracks WHERE date = :date")
    suspend fun getComment(date: Date): String?
}