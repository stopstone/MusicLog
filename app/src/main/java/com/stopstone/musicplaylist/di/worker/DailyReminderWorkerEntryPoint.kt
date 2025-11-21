package com.stopstone.musicplaylist.di.worker

import com.stopstone.musicplaylist.domain.usecase.home.GetTodayTrackUseCase
import com.stopstone.musicplaylist.domain.usecase.notification.IsDailyReminderEnabledUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DailyReminderWorkerEntryPoint {
    fun getTodayTrackUseCase(): GetTodayTrackUseCase

    fun getDailyReminderEnabledUseCase(): IsDailyReminderEnabledUseCase
}
