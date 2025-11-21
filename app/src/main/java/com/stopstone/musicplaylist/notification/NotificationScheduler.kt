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

    fun scheduleDailyMusicReminder(context: Context) {
        val workRequest = createPeriodicWorkRequest()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest,
        )
    }

    fun cancelDailyNotification(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    private fun createPeriodicWorkRequest(): PeriodicWorkRequest {
        val constraints =
            Constraints
                .Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

        val initialDelay = calculateInitialDelay()

        return PeriodicWorkRequestBuilder<DailyMusicReminderWorker>(
            24,
            TimeUnit.HOURS,
        ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()
    }

    private fun calculateInitialDelay(): Long {
        val calendar = Calendar.getInstance()
        val calendarHour = calendar.get(Calendar.HOUR)
        val calendarMinute = calendar.get(Calendar.MINUTE)

        val targetHour = 9
        val targetMinute = 0

        val targetCalendar =
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, targetHour)
                set(Calendar.MINUTE, targetMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

        if (calendarHour > targetHour ||
            (calendarHour == targetHour && calendarMinute >= targetMinute)
        ) {
            targetCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return targetCalendar.timeInMillis - System.currentTimeMillis()
    }
}
