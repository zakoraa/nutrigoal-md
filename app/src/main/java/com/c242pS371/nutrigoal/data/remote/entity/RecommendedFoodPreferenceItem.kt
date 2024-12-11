package com.c242pS371.nutrigoal.data.remote.entity

import com.google.gson.annotations.SerializedName

data class RecommendedFoodPreferenceItem(

    @field:SerializedName("Fiber(g)")
    val fiberG: Any? = null,

    @field:SerializedName("Carbohydrate(g)")
    val carbohydrateG: Float? = null,

    @field:SerializedName("rfp_id")
    val rfpId: String? = null,

    @field:SerializedName("rfp_updated_at")
    val rfpUpdatedAt: String? = null,

    @field:SerializedName("SaturatedFat(g)")
    val saturatedFatG: Any? = null,

    @field:SerializedName("Calories")
    val calories: Float? = null,

    @field:SerializedName("Fat(g)")
    val fatG: Float? = null,

    @field:SerializedName("Name")
    val name: String? = null,

    @field:SerializedName("Protein(g)")
    val proteinG: Float? = null,

    @field:SerializedName("Cholesterol(mg)")
    val cholesterolMg: Any? = null,

    @field:SerializedName("Sodium(mg)")
    val sodiumMg: Any? = null,

    @field:SerializedName("Sugar(g)")
    val sugarG: Any? = null,

    @field:SerializedName("rfboc_id")
    val rfbocId: String? = null,

    @field:SerializedName("rfp_created_at")
    val rfpCreatedAt: String? = null
)
