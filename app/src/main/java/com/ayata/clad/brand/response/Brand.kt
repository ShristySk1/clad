package com.ayata.clad.brand.response


import com.ayata.clad.home.response.ProductDetail
import com.google.gson.annotations.SerializedName

data class Brand(
    val address: String?="",
    @SerializedName("brand_cover")
    val brandCover: String="",
    @SerializedName("brand_image")
    val brandImage: String="",
    val description: String="",
    val id: Int,
    @SerializedName("items_sold")
    val itemsSold: Int,
    val name: String,
    val products: List<ProductDetail>,
    val slug: String,
    @SerializedName("total_reviews")
    val totalReviews: Int
)