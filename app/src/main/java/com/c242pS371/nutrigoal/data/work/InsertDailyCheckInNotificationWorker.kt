package com.c242pS371.nutrigoal.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.c242pS371.nutrigoal.R
import com.c242pS371.nutrigoal.data.local.database.NotificationDao
import com.c242pS371.nutrigoal.data.local.entity.NotificationLocalEntity
import com.c242pS371.nutrigoal.data.local.entity.NotificationType
import com.c242pS371.nutrigoal.utils.DateFormatter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

@HiltWorker
class InsertDailyCheckInNotificationWorker @AssistedInject constructor(
    @Assisted private val notificationDao: NotificationDao,
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val currentTime = Calendar.getInstance()

        val notification = NotificationLocalEntity(
            title = context.getString(R.string.check_in_reminder_title),
            isConfirmed = false,
            time = DateFormatter.formatToString(currentTime.time),
            notificationType = NotificationType.CHECK_IN
        )

        withContext(Dispatchers.IO) {
            notificationDao.insert(notification)
        }

        return Result.success()
    }
}

