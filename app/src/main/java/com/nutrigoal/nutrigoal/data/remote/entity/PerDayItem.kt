package com.nutrigoal.nutrigoal.data.remote.entity

data class PerDayItem(
    var id: String? = null,
    var foodRecommendation: List<FoodRecommendationItem>? = null,
    var bodyWeight: Float? = null,
    var age: Int? = null,
    var height: Float? = null,
    var createdAt: String? = null,
    var dietTime: String? = null
)