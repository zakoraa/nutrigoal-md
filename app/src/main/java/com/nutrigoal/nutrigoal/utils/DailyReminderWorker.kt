package com.nutrigoal.nutrigoal.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.data.local.entity.NotificationLocalEntity
import com.nutrigoal.nutrigoal.data.local.entity.NotificationType
import com.nutrigoal.nutrigoal.ui.MainActivity
import com.nutrigoal.nutrigoal.ui.notifications.NotificationsViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar

@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val notificationsViewModel: NotificationsViewModel,
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        saveNotificationToDatabase()
        showNotification()

        return Result.success()
    }

    private fun saveNotificationToDatabase() {
        val targetTime = getTargetTime()
        val formattedTime = DateFormatter.formatToString(targetTime.time)

        val notification = NotificationLocalEntity(
            id = System.currentTimeMillis().toInt(),
            title = context.getString(R.string.check_in_reminder_title),
            isConfirmed = false,
            time = formattedTime,
            notificationType = NotificationType.CHECK_IN
        )

        notificationsViewModel.insertNotification(notification)
    }

    private fun getTargetTime(): Calendar {
        return Calendar.getInstance()

//        return Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, 9)
//            set(Calendar.MINUTE, 0)
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//
//            if (currentTime.after(this)) {
//                add(Calendar.DAY_OF_YEAR, 1)
//            }
//        }
    }

    private fun showNotification() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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
            .setContentTitle(context.getString(R.string.check_in_reminder_title))
            .setContentText(context.getString(R.string.check_in_reminder_message))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(context.getString(R.string.check_in_reminder_message))
            )
            .addAction(
                R.drawable.ic_food,
                context.getString(R.string.open_app),
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
    }
}
