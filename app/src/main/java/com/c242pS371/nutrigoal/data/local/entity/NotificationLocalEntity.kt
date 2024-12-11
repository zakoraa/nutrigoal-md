package com.c242pS371.nutrigoal.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class NotificationLocalEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "time")
    var time: String = "",

    @ColumnInfo(name = "isConfirmed")
    var isConfirmed: Boolean = false,

    @ColumnInfo(name = "notificationType")
    var notificationType: NotificationType = NotificationType.CHECK_IN,
) : Parcelable

enum class NotificationType(private val displayName: String) {
    CHECK_IN("check_in"),
    TIME_TO_EAT("time_to_eat");

    override fun toString(): String {
        return displayName
    }
}