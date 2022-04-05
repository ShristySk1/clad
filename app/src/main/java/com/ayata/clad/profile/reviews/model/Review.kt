package com.ayata.clad.profile.reviews.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Review(
    @SerializedName("order_code")
    val orderCode: String,
    @SerializedName("order_date")
    val orderDate: String,
    val product: Product,
    @SerializedName("review_details")
    val reviewDetails: ReviewDetails
):Serializable