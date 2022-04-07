package com.ayata.clad.profile.reviews.response


import com.google.gson.annotations.SerializedName

data class Detail(
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String
)