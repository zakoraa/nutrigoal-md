package com.c242pS371.nutrigoal.data.remote.entity

import com.google.gson.annotations.SerializedName

data class FavoriteFoodName(

    @field:SerializedName("ffn_updated_at")
    val ffnUpdatedAt: String? = null,

    @field:SerializedName("ffn_created_at")
    val ffnCreatedAt: String? = null,

    @field:SerializedName("ffn_id")
    val ffnId: String? = null,

    @field:SerializedName("ffn_name")
    val ffnName: List<String>? = null
)