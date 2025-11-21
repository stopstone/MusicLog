package com.stopstone.musicplaylist.di

import com.stopstone.musicplaylist.data.repository.common.AuthRepositoryImpl
import com.stopstone.musicplaylist.data.repository.common.PlayListRepositoryImpl
import com.stopstone.musicplaylist.data.repository.common.TrackRepositoryImpl
import com.stopstone.musicplaylist.data.repository.emotion_setting.EmotionSettingRepositoryImpl
import com.stopstone.musicplaylist.data.repository.home.CalendarRepositoryImpl
import com.stopstone.musicplaylist.data.repository.insta_share.InstagramShareSettingRepositoryImpl
import com.stopstone.musicplaylist.data.repository.my.SignatureSongRepositoryImpl
import com.stopstone.musicplaylist.data.repository.notification.NotificationRepositoryImpl
import com.stopstone.musicplaylist.data.repository.search.SearchHistoryRepositoryImpl
import com.stopstone.musicplaylist.data.repository.search.SearchRepositoryImpl
import com.stopstone.musicplaylist.data.repository.user.UserProfileRepositoryImpl
import com.stopstone.musicplaylist.domain.repository.common.AuthRepository
import com.stopstone.musicplaylist.domain.repository.common.PlayListRepository
import com.stopstone.musicplaylist.domain.repository.common.TrackRepository
import com.stopstone.musicplaylist.domain.repository.emotion_setting.EmotionSettingRepository
import com.stopstone.musicplaylist.domain.repository.home.CalendarRepository
import com.stopstone.musicplaylist.domain.repository.insta_share.InstagramShareSettingRepository
import com.stopstone.musicplaylist.domain.repository.my.SignatureSongRepository
import com.stopstone.musicplaylist.domain.repository.notification.NotificationRepository
import com.stopstone.musicplaylist.domain.repository.search.SearchHistoryRepository
import com.stopstone.musicplaylist.domain.repository.search.SearchRepository
import com.stopstone.musicplaylist.domain.repository.user.UserProfileRepository
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
    abstract fun bindUserProfileRepository(
        repository: UserProfileRepositoryImpl,
    ): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindInstagramShareSettingRepository(
        repository: InstagramShareSettingRepositoryImpl,
    ): InstagramShareSettingRepository

    @Binds
    @Singleton
    abstract fun bindSignatureSongRepository(
        repository: SignatureSongRepositoryImpl,
    ): SignatureSongRepository

    @Binds
    @Singleton
    abstract fun bindEmotionSettingRepository(
        repository: EmotionSettingRepositoryImpl,
    ): EmotionSettingRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        repository: NotificationRepositoryImpl,
    ): NotificationRepository
}