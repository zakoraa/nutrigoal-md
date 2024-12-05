package com.nutrigoal.nutrigoal.data.local.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotificationDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT,
                $COLUMN_IS_CONFIRMED INTEGER,
                $COLUMN_TIME TEXT,
                $COLUMN_NOTIFICATION_TYPE INTEGER
            );
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "notification_database"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "notificationLocalEntity"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_IS_CONFIRMED = "isConfirmed"
        const val COLUMN_TIME = "time"
        const val COLUMN_NOTIFICATION_TYPE = "notificationType"
    }
}
