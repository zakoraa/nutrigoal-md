package com.c242pS371.nutrigoal.data.remote.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PerDayItem(
    var id: String? = null,
    var calorieNeeds: Float? = null,
    var mealSchedule: MealScheduleItem? = null,
    var foodRecommendation: List<FoodRecommendationItem>? = null,
    var selectedFoodRecommendation: List<FoodRecommendationItem>? = null,
    var bodyWeight: Float? = null,
    var age: Int? = null,
    var height: Float? = null,
    var activityLevel: Int? = null,
    var dietCategory: String? = null,
    var hasGastricIssue: Boolean? = null,
    var foodPreference: List<String>? = null,
    var createdAt: String? = null,
    var dietTime: String? = null
): Parcelable