package com.stopstone.myapplication.di

import com.stopstone.myapplication.data.repository.common.AuthRepositoryImpl
import com.stopstone.myapplication.data.repository.home.CalendarRepositoryImpl
import com.stopstone.myapplication.data.repository.common.PlayListRepositoryImpl
import com.stopstone.myapplication.data.repository.search.SearchHistoryRepositoryImpl
import com.stopstone.myapplication.data.repository.search.SearchRepositoryImpl
import com.stopstone.myapplication.data.repository.common.TrackRepositoryImpl
import com.stopstone.myapplication.data.repository.home.RecommendRepositoryImpl
import com.stopstone.myapplication.domain.repository.common.AuthRepository
import com.stopstone.myapplication.domain.repository.home.CalendarRepository
import com.stopstone.myapplication.domain.repository.common.PlayListRepository
import com.stopstone.myapplication.domain.repository.search.SearchHistoryRepository
import com.stopstone.myapplication.domain.repository.search.SearchRepository
import com.stopstone.myapplication.domain.repository.common.TrackRepository
import com.stopstone.myapplication.domain.repository.home.RecommendRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTrackRepository(
        repository: TrackRepositoryImpl
    ): TrackRepository

    @Binds
    @Singleton
    abstract fun bindCalendarRepository(
        repository: CalendarRepositoryImpl
    ): CalendarRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        repository: SearchRepositoryImpl
    ): SearchRepository

    @Binds
    @Singleton
    abstract fun bindPlayListRepository(
        repository: PlayListRepositoryImpl
    ): PlayListRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        repository: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSearchHistoryRepository(
        repository: SearchHistoryRepositoryImpl
    ): SearchHistoryRepository

    @Binds
    @Singleton
    abstract fun bindRecommendRepository(
        repository: RecommendRepositoryImpl
    ): RecommendRepository
}