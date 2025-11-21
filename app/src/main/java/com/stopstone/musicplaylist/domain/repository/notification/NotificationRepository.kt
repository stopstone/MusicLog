package com.stopstone.musicplaylist.domain.repository.notification

import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun observeDailyReminderEnabled(): Flow<Boolean>

    suspend fun setDailyReminderEnabled(enabled: Boolean)

    suspend fun isDailyReminderEnabled(): Boolean
}

