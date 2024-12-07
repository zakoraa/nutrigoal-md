package com.nutrigoal.nutrigoal.data.remote.response

import com.google.gson.annotations.SerializedName
import com.nutrigoal.nutrigoal.data.remote.entity.FavoriteFoodName
import com.nutrigoal.nutrigoal.data.remote.entity.FavoriteFoodPreference
import com.nutrigoal.nutrigoal.data.remote.entity.RecommendedFoodBasedOnCalories
import com.nutrigoal.nutrigoal.data.remote.entity.RecommendedFoodPreferenceItem

data class SurveyResponse(

	@field:SerializedName("favorite_food_preference")
	val favoriteFoodPreference: FavoriteFoodPreference? = null,

	@field:SerializedName("recommended_food_preference")
	val recommendedFoodPreference: List<RecommendedFoodPreferenceItem?>? = null,

	@field:SerializedName("favorite_food_name")
	val favoriteFoodName: FavoriteFoodName? = null,

	@field:SerializedName("recommended_food_based_on_calories")
	val recommendedFoodBasedOnCalories: RecommendedFoodBasedOnCalories? = null
)
