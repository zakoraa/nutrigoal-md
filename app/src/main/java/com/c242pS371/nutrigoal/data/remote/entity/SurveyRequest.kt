package com.c242pS371.nutrigoal.data.remote.entity

data class SurveyRequest(
    val age: Int,
    val height: Float,
    val weight: Float?,
    val gender: Boolean,
    val activity_level: Int,
    val diet_category: String,
    val has_gastric_issue: Boolean,
    val food_preference: List<String>
)
