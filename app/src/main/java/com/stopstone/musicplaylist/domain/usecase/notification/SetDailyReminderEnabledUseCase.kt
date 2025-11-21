package com.stopstone.musicplaylist.domain.usecase.notification

import com.stopstone.musicplaylist.domain.repository.notification.NotificationRepository
import javax.inject.Inject

class SetDailyReminderEnabledUseCase
    @Inject
    constructor(
        private val notificationRepository: NotificationRepository,
    ) {
        suspend operator fun invoke(enabled: Boolean) {
            notificationRepository.setDailyReminderEnabled(enabled)
        }
    }

