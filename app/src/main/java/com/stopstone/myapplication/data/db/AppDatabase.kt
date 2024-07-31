package com.stopstone.myapplication.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.stopstone.myapplication.data.model.DailyTrack
import com.stopstone.myapplication.data.model.SearchHistory
import com.stopstone.myapplication.util.Converters

@Database(entities = [DailyTrack::class, SearchHistory::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyTrackDao(): DailyTrackDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}