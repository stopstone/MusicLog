package com.stopstone.musicplaylist.data.repository.notification

import com.stopstone.musicplaylist.data.local.settings.NotificationPreferences
import com.stopstone.musicplaylist.domain.repository.notification.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepositoryImpl
    @Inject
    constructor(
        private val notificationPreferences: NotificationPreferences,
    ) : NotificationRepository {
        override fun observeDailyReminderEnabled(): Flow<Boolean> =
            notificationPreferences.observeDailyReminderEnabled()

        override suspend fun setDailyReminderEnabled(enabled: Boolean) {
            notificationPreferences.setDailyReminderEnabled(enabled)
        }

        override suspend fun isDailyReminderEnabled(): Boolean {
            return notificationPreferences.isDailyReminderEnabled()
        }
    }

