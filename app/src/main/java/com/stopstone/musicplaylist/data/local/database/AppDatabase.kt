package com.stopstone.musicplaylist.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.stopstone.musicplaylist.data.local.dao.DailyTrackDao
import com.stopstone.musicplaylist.data.local.dao.SignatureSongDao
import com.stopstone.musicplaylist.data.local.dao.SearchHistoryDao
import com.stopstone.musicplaylist.data.model.entity.DailyTrack
import com.stopstone.musicplaylist.data.model.entity.SignatureSong
import com.stopstone.musicplaylist.data.model.entity.SearchHistory
import com.stopstone.musicplaylist.util.Converters

@Database(
    entities = [DailyTrack::class, SearchHistory::class, SignatureSong::class],
    version = 2,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyTrackDao(): DailyTrackDao

    abstract fun searchHistoryDao(): SearchHistoryDao

    abstract fun lifeSongDao(): SignatureSongDao
}
