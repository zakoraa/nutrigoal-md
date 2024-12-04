package com.nutrigoal.nutrigoal.utils

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
import com.nutrigoal.nutrigoal.R
import com.nutrigoal.nutrigoal.ui.MainActivity
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
        var currentMealIndex = 0

        handler.postDelayed(object : Runnable {
            override fun run() {
                val (mealTitle, mealTime) = times[currentMealIndex]
                val countdown = calculateCountdown(mealTime)

                updateNotification(mealTitle, countdown)

                if (countdown == "00:00:00") {
                    currentMealIndex = (currentMealIndex + 1) % times.size
                }

                handler.postDelayed(this, 1000L)
            }
        }, 1000L)
    }

    private fun getMealTimes(): List<Pair<String, String>> {
        val breakfastTime = getUserMealTime("Breakfast", "09:00 AM")
        val lunchTime = getUserMealTime("Lunch", "02:00 PM")
        val dinnerTime = getUserMealTime("Dinner", "08:00 PM")

        return listOf(
            "Time to enjoy your Breakfast at $breakfastTime!" to breakfastTime,
            "Let's have a great Lunch at $lunchTime!" to lunchTime,
            "Dinner is waiting for you at $dinnerTime!" to dinnerTime
        )
    }

    private fun getUserMealTime(meal: String, defaultTime: String): String {
        val sharedPreferences =
            applicationContext.getSharedPreferences("MealTimes", Context.MODE_PRIVATE)
        return sharedPreferences.getString(meal, defaultTime) ?: defaultTime
    }

    private fun calculateCountdown(targetTime: String): String {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val currentTime = Calendar.getInstance(Locale.ENGLISH)

        val targetCalendar = Calendar.getInstance(Locale.ENGLISH).apply {
            time = dateFormat.parse(targetTime)!!
            if (before(currentTime)) add(Calendar.DAY_OF_YEAR, 1)
        }

        val diffMillis = targetCalendar.timeInMillis - currentTime.timeInMillis

        val hours = (diffMillis / (1000 * 60 * 60)) % 24 + 24
        val minutes = (diffMillis / (1000 * 60) % 60).toInt() + 60
        val seconds = (diffMillis / 1000 % 60).toInt() + 60

        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds)
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
