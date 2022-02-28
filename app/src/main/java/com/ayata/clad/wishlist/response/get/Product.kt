package com.ayata.clad.wishlist.response.get


import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String,
    val name: String,
    val price: Double,
    val vendor: String
)