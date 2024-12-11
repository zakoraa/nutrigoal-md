package com.c242pS371.nutrigoal.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.c242pS371.nutrigoal.data.local.entity.NotificationLocalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notificationLocalEntity: NotificationLocalEntity)

    @Query("SELECT * FROM notificationLocalEntity ORDER BY id ASC")
    fun getAllNotifications(): Flow<List<NotificationLocalEntity>>

    @Query("UPDATE notificationLocalEntity SET isConfirmed = 1 WHERE id = :id")
    fun updateIsConfirmed(id: Int?)
}