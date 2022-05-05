package com.ayata.clad.home.response


import com.ayata.clad.brand.response.Brand
import com.ayata.clad.shop.response.ChildCategory
import com.google.gson.annotations.SerializedName

data class Slider(
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String,
    val title: String,
    @SerializedName("slider_type")
    val sliderType:String,
    val category: ChildCategory,
    val product: ProductDetail,
    val brand: Brand
)