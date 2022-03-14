package com.ayata.clad.home.response


import com.google.gson.annotations.SerializedName

data class Reviews(
    val comfort: Double,
    val quality: Double,
    val rating: Double,
    @SerializedName("recommended_by")
    val recommendedBy: String,
    val size: String,
    @SerializedName("total_review")
    val totalReview: Int,
    val width: String
)