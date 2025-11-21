package com.stopstone.musicplaylist.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.stopstone.musicplaylist.domain.usecase.notification.IsDailyReminderEnabledUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val entryPoint =
                EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    BootReceiverEntryPoint::class.java,
                )
            val isEnabled =
                runBlocking {
                    entryPoint.isDailyReminderEnabledUseCase().invoke()
                }
            if (isEnabled) {
                NotificationScheduler.scheduleDailyMusicReminder(context)
            }
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BootReceiverEntryPoint {
        fun isDailyReminderEnabledUseCase(): IsDailyReminderEnabledUseCase
    }
}
