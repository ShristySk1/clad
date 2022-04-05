package com.ayata.clad.profile.reviews.model


import com.google.gson.annotations.SerializedName

data class Product(
    val color: String,
    @SerializedName("dollar_price")
    val dollarPrice: Double,
    val name: String,
    val price: Double,
    val quantity: Int,
    val size: String,
    @SerializedName("variant_id")
    val variantId: Int,
    val image_url:String
)