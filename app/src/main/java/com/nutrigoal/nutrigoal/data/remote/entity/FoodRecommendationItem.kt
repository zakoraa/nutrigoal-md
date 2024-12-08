package com.nutrigoal.nutrigoal.data.remote.entity

data class FoodRecommendationItem(
    var id: String? = null,
    var name: String? = null,
    var calories: Int? = null,
    var protein: Float? = null,
    var carbohydrate: Float? = null,
    var fat: Float? = null
)