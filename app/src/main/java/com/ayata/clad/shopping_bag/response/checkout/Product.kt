package com.ayata.clad.shopping_bag.response.checkout


import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String="",
    val name: String="",
    val price: Double=0.0,
    val quantity: Int=0,
    val variant: List<Variant>?,
    val vendor: String=""
)