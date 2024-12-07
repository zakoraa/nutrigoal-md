package com.nutrigoal.nutrigoal.data.remote.entity

data class SurveyRequest(
    val age: Int,
    val height: Float,
    val weight: Float,
    val gender: Int,
    val activity_level: Int,
    val diet_category: String,
    val has_gastric_issue: String,
    val food_preference: List<String>
)
