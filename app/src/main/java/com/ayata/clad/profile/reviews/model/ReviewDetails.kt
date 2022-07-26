package com.ayata.clad.profile.reviews.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReviewDetails(
    val customer: String,
    val description: String,
    val size: String,
    val comfort: String,
    val quality: Int,
    @SerializedName("image_url")
    val imageUrl: List<ImageReview>,
    @SerializedName("is_reviewed")
    val isReviewed: Boolean,
    val rate: Double,
    @SerializedName("reviewed_date")
    val reviewedDate: String
) : Serializable