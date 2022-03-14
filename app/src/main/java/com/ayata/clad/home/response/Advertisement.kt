package com.ayata.clad.home.response


import com.google.gson.annotations.SerializedName

data class Advertisement(
    @SerializedName("ad_position")
    val adPosition: String,
    @SerializedName("ad_type")
    val adType: String,
    val brand: Any,
    val category: Category,
    val description: String,
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String,
    val product: Any,
    val tag: Any,
    val title: String
)