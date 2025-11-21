package com.stopstone.musicplaylist.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    private const val WORK_NAME = "daily_music_reminder_work"
    private const val REMINDER_INTERVAL_HOURS = 24L
    private const val REMINDER_TARGET_HOUR = 21
    private const val REMINDER_TARGET_MINUTE = 0

    fun scheduleDailyMusicReminder(context: Context) {
        val initialDelay = calculateInitialDelay()
        val workRequest = createPeriodicWorkRequest(initialDelay)
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest,
        )
    }

    fun cancelDailyNotification(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    private fun createPeriodicWorkRequest(initialDelay: Long): PeriodicWorkRequest {
        val constraints =
            Constraints
                .Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

        return PeriodicWorkRequestBuilder<DailyMusicReminderWorker>(
            REMINDER_INTERVAL_HOURS,
            TimeUnit.HOURS,
        ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()
    }

    private fun calculateInitialDelay(): Long {
        val calendar = Calendar.getInstance()
        val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)
        val calendarMinute = calendar.get(Calendar.MINUTE)
        val targetCalendar =
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, REMINDER_TARGET_HOUR)
                set(Calendar.MINUTE, REMINDER_TARGET_MINUTE)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        val hasPassedTargetHour = calendarHour > REMINDER_TARGET_HOUR
        val hasPassedTargetMinute = calendarHour == REMINDER_TARGET_HOUR && calendarMinute >= REMINDER_TARGET_MINUTE
        if (hasPassedTargetHour || hasPassedTargetMinute) {
            targetCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        val delay = targetCalendar.timeInMillis - System.currentTimeMillis()
        return delay
    }
}
