package com.stopstone.musicplaylist.domain.usecase.notification

import com.stopstone.musicplaylist.domain.repository.notification.NotificationRepository
import javax.inject.Inject

class IsDailyReminderEnabledUseCase
    @Inject
    constructor(
        private val notificationRepository: NotificationRepository,
    ) {
        suspend operator fun invoke(): Boolean = notificationRepository.isDailyReminderEnabled()
    }

