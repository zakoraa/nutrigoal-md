package com.c242pS371.nutrigoal.data.remote.entity

import com.google.gson.annotations.SerializedName

data class RecommendedFoodBasedOnCalories(

    @field:SerializedName("rfboc_total_cholesterol_(mg)")
    val rfbocTotalCholesterolMg: String? = null,

    @field:SerializedName("rfboc_total_sodium_(mg)")
    val rfbocTotalSodiumMg: String? = null,

    @field:SerializedName("rfboc_total_calories_by_recommendation")
    val rfbocTotalCaloriesByRecommendation: String? = null,

    @field:SerializedName("rfboc_total_fiber_(g)")
    val rfbocTotalFiberG: String? = null,

    @field:SerializedName("rfboc_weight_(kg)")
    val rfbocWeightKg: Float? = null,

    @field:SerializedName("rfboc_gender")
    val rfbocGender: Boolean? = null,

    @field:SerializedName("rfboc_bmr")
    val rfbocBmr: String? = null,

    @field:SerializedName("rfboc_history_of_gastritis_or_gerd")
    val rfbocHistoryOfGastritisOrGerd: Boolean? = null,

    @field:SerializedName("rfboc_bmi")
    val rfbocBmi: String? = null,

    @field:SerializedName("rfboc_meal_schedule(day)")
    val rfbocMealScheduleDay: String? = null,

    @field:SerializedName("rfboc_weight_difference")
    val rfbocWeightDifference: String? = null,

    @field:SerializedName("rfboc_age")
    val rfbocAge: Int? = null,

    @field:SerializedName("rfboc_ideal_bmi")
    val rfbocIdealBmi: String? = null,

    @field:SerializedName("rfboc_ideal_weight")
    val rfbocIdealWeight: String? = null,

    @field:SerializedName("rfboc_total_carbohydrate_(g)")
    val rfbocTotalCarbohydrateG: String? = null,

    @field:SerializedName("rfboc_total_sugar_(g)")
    val rfbocTotalSugarG: String? = null,

    @field:SerializedName("rfboc_total_protein_(g)")
    val rfbocTotalProteinG: String? = null,

    @field:SerializedName("rfboc_daily_calorie_needs")
    val rfbocDailyCalorieNeeds: String? = null,

    @field:SerializedName("rfboc_total_fat_(g)")
    val rfbocTotalFatG: String? = null,

    @field:SerializedName("rfboc_height_(cm)")
    val rfbocHeightCm: Float? = null,

    @field:SerializedName("rfboc_diet_type")
    val rfbocDietType: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("rfboc_total_saturated_fat_(g)")
    val rfbocTotalSaturatedFatG: String? = null,

    @field:SerializedName("rfboc_activity_level")
    val rfbocActivityLevel: Int? = null,

    @field:SerializedName("rfboc_id")
    val rfbocId: String? = null
)
