package com.ayata.clad.shopping_bag.response.checkout


import com.google.gson.annotations.SerializedName

data class Category(
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String,
    val title: String
)