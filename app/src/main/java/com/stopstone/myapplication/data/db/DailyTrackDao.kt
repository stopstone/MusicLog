package com.stopstone.myapplication.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stopstone.myapplication.data.model.DailyTrack
import java.util.Date

@Dao
interface DailyTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyTrack: DailyTrack)

    @Query("SELECT * FROM daily_tracks WHERE date = :date")
    suspend fun getDailyTrack(date: Date): DailyTrack?
}