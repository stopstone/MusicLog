package com.stopstone.myapplication.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.stopstone.myapplication.data.model.DailyTrack

@Dao
interface DailyTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // primary가 충돌이 발생했을 때 데이터를 대체
    suspend fun insertDailyTrack(dailyTrack: DailyTrack)
}