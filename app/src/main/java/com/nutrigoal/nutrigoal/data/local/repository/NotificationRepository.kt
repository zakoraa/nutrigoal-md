package com.nutrigoal.nutrigoal.data.local.repository

import com.nutrigoal.nutrigoal.data.ResultState
import com.nutrigoal.nutrigoal.data.local.database.NotificationDao
import com.nutrigoal.nutrigoal.data.local.entity.NotificationLocalEntity
import com.nutrigoal.nutrigoal.data.extension.asResultState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val mNotificationDao: NotificationDao
) {

    fun getAllNotifications(): Flow<ResultState<List<NotificationLocalEntity>>> =
        mNotificationDao.getAllNotifications()
            .asResultState()

    suspend fun updateIsConfirmed(id: Int?) {
        mNotificationDao.updateIsConfirmed(id)
    }

    suspend fun insert(notificationLocalEntity: NotificationLocalEntity) {
        mNotificationDao.insert(notificationLocalEntity)
    }
}
