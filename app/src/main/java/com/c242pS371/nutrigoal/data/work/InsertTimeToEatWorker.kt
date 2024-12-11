package com.c242pS371.nutrigoal.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.c242pS371.nutrigoal.data.local.database.NotificationDao
import com.c242pS371.nutrigoal.data.local.entity.NotificationLocalEntity
import com.c242pS371.nutrigoal.data.local.entity.NotificationType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@HiltWorker
class InsertTimeToEatWorker @AssistedInject constructor(
    @Assisted private val notificationDao: NotificationDao,
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val mealTitle = inputData.getString("mealTitle") ?: return Result.failure()

        val dateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.ENGLISH)
        val currentTime = Calendar.getInstance().time
        val formattedTime = dateFormat.format(currentTime)

        val notificationEntity = NotificationLocalEntity(
            title = mealTitle,
            notificationType = NotificationType.TIME_TO_EAT,
            isConfirmed = false,
            time = formattedTime
        )

        notificationDao.insert(notificationEntity)
        return Result.success()
    }
}
