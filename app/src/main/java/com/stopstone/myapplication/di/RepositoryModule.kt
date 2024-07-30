package com.stopstone.myapplication.di

import com.stopstone.myapplication.data.repository.AuthRepositoryImpl
import com.stopstone.myapplication.data.repository.CalendarRepositoryImpl
import com.stopstone.myapplication.data.repository.PlayListRepositoryImpl
import com.stopstone.myapplication.data.repository.SearchRepositoryImpl
import com.stopstone.myapplication.data.repository.TrackRepositoryImpl
import com.stopstone.myapplication.domain.repository.AuthRepository
import com.stopstone.myapplication.domain.repository.CalendarRepository
import com.stopstone.myapplication.domain.repository.PlayListRepository
import com.stopstone.myapplication.domain.repository.SearchRepository
import com.stopstone.myapplication.domain.repository.TrackRepository
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
}