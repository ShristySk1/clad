package com.ayata.clad.home.response


import com.ayata.clad.profile.reviews.response.Detail
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SingleReview(
    @SerializedName("reviewed_date")
    val createdAt: String,
    val customer: String,
    val rate: Double,
    @SerializedName("description")
    val review: String,
    val image_url:List<Detail>
):Serializable