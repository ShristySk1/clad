package com.ayata.clad.home.response


import com.google.gson.annotations.SerializedName

data class Content(
    val description: String,
    @SerializedName("image_url")
    val imageUrl: String,
    val products: List<ProductDetail>,
    val title: String
)