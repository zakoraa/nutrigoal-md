package com.c242pS371.nutrigoal.data.work

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager

class StopAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        WorkManager.getInstance(context).cancelAllWorkByTag("SoundWorker")

        TimeToEatAlarmWorker.mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
                TimeToEatAlarmWorker.mediaPlayer = null
            }
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(TimeToEatAlarmWorker.NOTIFICATION_ID)
    }
}

