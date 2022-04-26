package com.ayata.clad.home.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Reviews(
    val comfort: String?,
    val quality: Double,
    val rating: Double?=0.0,
    @SerializedName("recommended_by")
    val recommendedBy: String,
    val size: String?,
    @SerializedName("total_review")
    val totalReview: Int,
    val width: String?,
    val review_details:List<SingleReview>?
):Serializable