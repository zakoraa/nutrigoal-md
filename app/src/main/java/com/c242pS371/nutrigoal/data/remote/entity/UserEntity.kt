package com.c242pS371.nutrigoal.data.remote.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserEntity(
    val id: String? = null,
    val photoProfile: String? = null,
    val username: String? = null,
    val email: String? = null,
    var age: Int? = null,
    var gender: Boolean? = null,
    var bodyWeight: Float? = null,
    var height: Float? = null,
    var activityLevel: Int? = null,
    var dietCategory: String? = null,
    var hasGastricIssue: Boolean? = null,
    var foodPreference: List<String>? = null,
    var mealSchedule: MealScheduleItem? = null
) : Parcelable

enum class Gender(private val displayName: String) {
    MALE("Male"),
    FEMALE("Female");

    override fun toString(): String {
        return displayName
    }
}

enum class DietCategory(private val displayName: String) {
    VEGAN("vegan"),
    KETO("keto");

    override fun toString(): String {
        return displayName
    }
}