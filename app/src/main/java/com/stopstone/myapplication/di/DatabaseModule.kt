package com.stopstone.myapplication.di

import android.content.Context
import androidx.room.Room
import com.stopstone.myapplication.data.db.AppDatabase
import com.stopstone.myapplication.data.db.DailyTrackDao
import com.stopstone.myapplication.data.db.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideDailyTrackDao(database: AppDatabase): DailyTrackDao {
        return database.dailyTrackDao()
    }

    @Provides
    fun provideSearchHistoryDao(database: AppDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }
}