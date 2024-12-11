package com.c242pS371.nutrigoal.data.remote.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoodRecommendationItem(
    var id: String? = null,
    var name: String? = null,
    var calories: Float? = null,
    var protein: Float? = null,
    var carbohydrate: Float? = null,
    var fat: Float? = null
) : Parcelable