package com.nutrigoal.nutrigoal.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getString
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.ui.MainActivity

class DailyReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            putExtra(EXTRA_NAVIGATE_KEY, EXTRA_NAVIGATE_VALUE)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val resource = R.drawable.ic_food
        val largeIcon = BitmapFactory.decodeResource(applicationContext.resources, resource)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setLargeIcon(largeIcon)
            .setSmallIcon(R.drawable.nutrigoal_notification_logo)
            .setContentTitle(getString(context, R.string.eat_reminder_title))
            .setContentText(getString(context, R.string.eat_reminder_message))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(getString(context, R.string.eat_reminder_message))
            )
            .addAction(
                R.drawable.ic_food,
                getString(context, R.string.open_app),
                pendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "daily_reminder_channel"
        private const val CHANNEL_NAME = "Daily Reminder"
//        const val EXTRA_NAVIGATE_KEY = "navigate_to"
//        const val EXTRA_NAVIGATE_VALUE = "NotificationFragment"
    }
}
