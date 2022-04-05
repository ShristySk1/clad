package com.ayata.clad.profile.reviews.model


import com.google.gson.annotations.SerializedName

data class ReviewDetails(
    val customer: String,
    val description: String,
    @SerializedName("image_url")
    val imageUrl: List<String>,
    @SerializedName("is_reviewed")
    val isReviewed: Boolean,
    val rate: Double,
    @SerializedName("reviewed_date")
    val reviewedDate: String
)