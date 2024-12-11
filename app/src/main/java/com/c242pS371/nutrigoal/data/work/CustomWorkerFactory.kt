package com.c242pS371.nutrigoal.data.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.c242pS371.nutrigoal.data.local.database.NotificationDao
import javax.inject.Inject

class CustomWorkerFactory @Inject constructor(private val notificationDao: NotificationDao) :
    WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            InsertDailyCheckInNotificationWorker::class.java.name -> {
                InsertDailyCheckInNotificationWorker(notificationDao, appContext, workerParameters)
            }

            InsertTimeToEatWorker::class.java.name -> {
                InsertTimeToEatWorker(notificationDao, appContext, workerParameters)
            }

            DailyReminderWorker::class.java.name -> {
                DailyReminderWorker(appContext, workerParameters)
            }

            else -> null
        }
    }
}