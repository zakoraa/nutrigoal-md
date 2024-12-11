package com.c242pS371.nutrigoal.data.remote.entity

import com.google.gson.annotations.SerializedName

data class FavoriteFoodPreference(

    @field:SerializedName("ffn_id")
    val ffnId: String? = null,

    @field:SerializedName("ffp_created_at")
    val ffpCreatedAt: String? = null,

    @field:SerializedName("ffp_id")
    val ffpId: String? = null,

    @field:SerializedName("ffp_name")
    val ffpName: List<String?>? = null,

    @field:SerializedName("ffp_updated_at")
    val ffpUpdatedAt: String? = null
)
