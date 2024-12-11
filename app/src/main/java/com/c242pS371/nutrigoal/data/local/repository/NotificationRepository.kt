package com.c242pS371.nutrigoal.data.local.repository

import com.c242pS371.nutrigoal.data.ResultState
import com.c242pS371.nutrigoal.data.local.database.NotificationDao
import com.c242pS371.nutrigoal.data.local.entity.NotificationLocalEntity
import com.c242pS371.nutrigoal.data.extension.asResultState
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
