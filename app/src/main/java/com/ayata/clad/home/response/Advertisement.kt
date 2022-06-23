package com.ayata.clad.home.response


import com.ayata.clad.shop.response.ChildCategory
import com.google.gson.annotations.SerializedName

data class Advertisement(
    @SerializedName("ad_position")
    val adPosition: String,
    @SerializedName("ad_type")
    val adType: String,
    val brand: Brand?,
    val category: ChildCategory?,
    val description: String,
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String,
    val product: ProductDetail?,
    val title: String
)
