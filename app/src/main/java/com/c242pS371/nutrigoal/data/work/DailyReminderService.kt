package com.c242pS371.nutrigoal.data.work

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.ui.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DailyReminderService : Service() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        startForeground(NOTIFICATION_ID, buildNotification())

        startCountdown()
    }

    override fun onDestroy() {
        stopSelf()
        stopForeground(STOP_FOREGROUND_DETACH)
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startCountdown() {
        val times = getMealTimes()
        val currentTime = Calendar.getInstance(Locale.ENGLISH)

        var currentMealIndex = if (getFirstDietTIme()) {
            0
        } else {
            getNextMealTimeIndex(times, currentTime)
        }

        handler.postDelayed(object : Runnable {
            override fun run() {
                val (mealTitle, mealTime) = times[currentMealIndex]
                val countdown = calculateCountdown(mealTime)

                if (countdown == "00:00:00") {
                    val workRequest = OneTimeWorkRequest.Builder(TimeToEatAlarmWorker::class.java)
                        .addTag("SoundWorker")
                        .build()
                    WorkManager.getInstance(applicationContext).enqueue(workRequest)
                    insertNotificationToDatabase(mealTitle)
                    currentMealIndex = (currentMealIndex + 1) % times.size
                }

                updateNotification(mealTitle, countdown)

                handler.postDelayed(this, 1000L)
            }
        }, 1000L)
    }

    private fun getNextMealTimeIndex(
        times: List<Pair<String, String>>,
        currentTime: Calendar
    ): Int {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

        val formattedCurrentTime =
            SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(currentTime.time)
        val currentCalendar = Calendar.getInstance(Locale.ENGLISH).apply {
            time = dateFormat.parse(formattedCurrentTime)!!
        }

        for (i in times.indices) {
            val (_, mealTime) = times[i]

            val targetCalendar = Calendar.getInstance(Locale.ENGLISH).apply {
                time = dateFormat.parse(mealTime)!!
            }

            if (currentCalendar.after(targetCalendar)) {
                if (i == times.size - 1) {
                    return 0
                }
                continue
            }
            return i
        }

        return 0
    }


    private fun insertNotificationToDatabase(title: String) {
        val workRequest: WorkRequest =
            OneTimeWorkRequest.Builder(InsertTimeToEatWorker::class.java)
                .setInputData(workDataOf("mealTitle" to title))
                .build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }

    private fun calculateCountdown(targetTime: String): String {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val currentTime = Calendar.getInstance(Locale.ENGLISH)

        val targetCalendar = Calendar.getInstance(Locale.ENGLISH).apply {
            time = dateFormat.parse(targetTime)!!
            if (before(currentTime)) add(Calendar.DAY_OF_YEAR, 1)
        }

        val diffMillis = targetCalendar.timeInMillis - currentTime.timeInMillis

        var hours = (diffMillis / (1000 * 60 * 60)) % 24 + 23
        var minutes = (diffMillis / (1000 * 60) % 60).toInt() + 59
        var seconds = (diffMillis / 1000 % 60).toInt() + 59

        if (hours.toInt() == 60) {
            hours = 0
        }
        if (minutes == 60) {
            minutes = 0
        }
        if (seconds == 60) {
            seconds = 0
        }

        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun getMealTimes(): List<Pair<String, String>> {
        val times = mutableListOf<Pair<String, String>>()

        val breakfastTime = getUserMealTime("Breakfast")
        val lunchTime = getUserMealTime("Lunch")
        val dinnerTime = getUserMealTime("Dinner")

        breakfastTime?.let {
            times.add("Time to enjoy your Breakfast at $it!" to it)
        }
        lunchTime?.let {
            times.add("Let's have a great Lunch at $it!" to it)
        }
        dinnerTime?.let {
            times.add("Dinner is waiting for you at $it!" to it)
        }

        return times
    }


    private fun getUserMealTime(meal: String): String? {
        val sharedPreferences =
            applicationContext.getSharedPreferences("MealTimes", Context.MODE_PRIVATE)
        return sharedPreferences.getString(meal, null)
    }

    private fun getFirstDietTIme(): Boolean {
        val sharedPreferences =
            applicationContext.getSharedPreferences("firstDietTime", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isNew", false)
    }


    private fun updateNotification(title: String, countdown: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.nutrigoal_notification_logo)
            .setBadgeIconType(R.drawable.ic_food)
            .setContentTitle(title)
            .setContentText(countdown)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(): Notification {

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.nutrigoal_notification_logo)
            .setBadgeIconType(R.drawable.ic_food)
            .setContentTitle("Meal Reminder")
            .setContentText("Starting countdown...")
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onBind(intent: Intent?) = null

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "meal_reminder_channel"
        private const val CHANNEL_NAME = "Meal Reminder"
    }
}
