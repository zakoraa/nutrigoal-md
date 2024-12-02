package com.nutrigoal.nutrigoal.data.remote.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserEntity(
    val photoProfile: String?,
    val username: String,
    val email: String,
    val age: Int,
    val gender: Gender,
    val bodyWeight: Float,
    val height: Float,
): Parcelable

enum class Gender(private val displayName: String) {
    MALE("Male"),
    FEMALE("Female");

    override fun toString(): String {
        return displayName
    }
}