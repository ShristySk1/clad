package com.ayata.clad.home.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SingleReview(
    @SerializedName("created_at")
    val createdAt: String,
    val customer: String,
    val rate: Int,
    val review: String
):Serializable