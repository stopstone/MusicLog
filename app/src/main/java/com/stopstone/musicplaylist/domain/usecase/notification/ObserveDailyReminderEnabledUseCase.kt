package com.stopstone.musicplaylist.domain.usecase.notification

import com.stopstone.musicplaylist.domain.repository.notification.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDailyReminderEnabledUseCase
    @Inject
    constructor(
        private val notificationRepository: NotificationRepository,
    ) {
        operator fun invoke(): Flow<Boolean> = notificationRepository.observeDailyReminderEnabled()
    }

