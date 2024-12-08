package com.nutrigoal.nutrigoal.data.remote.entity

data class PerDayItem(
    var id: String? = null,
    var foodRecommendation: List<FoodRecommendationItem>? = null,
    var bodyWeight: Float? = null,
    var age: Int? = null,
    var height: Float? = null,
    var activityLevel: String? = null,
    var dietCategory: String? = null,
    var hasGastricIssue: String? = null,
    var foodPreference: List<String>? = null,
    var createdAt: String? = null,
    var dietTime: String? = null
)