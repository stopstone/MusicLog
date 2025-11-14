package com.stopstone.musicplaylist.di

import android.content.Context
import androidx.room.Room
import com.stopstone.musicplaylist.data.local.dao.DailyTrackDao
import com.stopstone.musicplaylist.data.local.dao.SignatureSongDao
import com.stopstone.musicplaylist.data.local.dao.SearchHistoryDao
import com.stopstone.musicplaylist.data.local.database.AppDatabase
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
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                "app_database",
            ).fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideDailyTrackDao(database: AppDatabase): DailyTrackDao = database.dailyTrackDao()

    @Provides
    fun provideSearchHistoryDao(database: AppDatabase): SearchHistoryDao = database.searchHistoryDao()

    @Provides
    fun provideSignatureSongDao(database: AppDatabase): SignatureSongDao = database.lifeSongDao()
}
