package com.c242pS371.nutrigoal.data.remote.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MealScheduleItem(
    var breakfastTime: String? = null,
    var launchTime: String? = null,
    var dinnerTime: String? = null
) : Parcelable
