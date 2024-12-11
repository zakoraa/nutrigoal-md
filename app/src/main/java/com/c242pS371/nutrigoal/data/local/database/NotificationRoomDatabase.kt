package com.c242pS371.nutrigoal.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.c242pS371.nutrigoal.data.local.entity.NotificationLocalEntity

@Database(entities = [NotificationLocalEntity::class], version = 1)
abstract class NotificationLocalEntityRoomDatabase : RoomDatabase() {
    abstract fun NotificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: NotificationLocalEntityRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): NotificationLocalEntityRoomDatabase {
            if (INSTANCE == null) {
                synchronized(NotificationLocalEntityRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        NotificationLocalEntityRoomDatabase::class.java, "notification_database"
                    )
                        .build()
                }
            }
            return INSTANCE as NotificationLocalEntityRoomDatabase
        }
    }
}