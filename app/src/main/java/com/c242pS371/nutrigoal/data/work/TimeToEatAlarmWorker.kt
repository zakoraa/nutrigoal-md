package com.c242pS371.nutrigoal.data.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.c242pS371.nutrigoal.R

class TimeToEatAlarmWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        var mediaPlayer: MediaPlayer? = null
        private const val CHANNEL_ID = "alarm_notification_channel"
        const val NOTIFICATION_ID = 2001
    }

    override fun doWork(): Result {
        createNotificationChannel()

        val stopIntent = Intent(applicationContext, StopAlarmReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.nutrigoal_notification_logo)
            .setContentTitle(applicationContext.getString(R.string.time_to_eat_notif_title))
            .setContentText(applicationContext.getString(R.string.time_to_eat_notif_desc))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(applicationContext.getString(R.string.time_to_eat_notif_desc))
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_clock,
                applicationContext.getString(R.string.stop),
                stopPendingIntent
            )
            .build()

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)

        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.reminder).apply {
            isLooping = true
            start()
        }

        return Result.success()
    }

    override fun onStopped() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onStopped()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
