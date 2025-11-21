package com.stopstone.musicplaylist.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.ui.MainActivity

class DailyMusicReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result =
        try {
            showNotification()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }

    private fun showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent =
            Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        val pendingIntent =
            PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        val notification =
            NotificationCompat
                .Builder(
                    applicationContext,
                    NotificationChannelManager.getChannelId(),
                ).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(applicationContext.getString(R.string.app_name))
                .setContentText(applicationContext.getString(R.string.daily_music_reminder_content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (!notificationManager.areNotificationsEnabled()) {
            return
        }
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}
