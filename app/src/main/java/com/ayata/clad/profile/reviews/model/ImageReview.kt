package com.ayata.clad.profile.reviews.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ImageReview(
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String
) : Serializable
