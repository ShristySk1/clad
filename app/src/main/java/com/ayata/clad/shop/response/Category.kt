package com.ayata.clad.shop.response


import com.google.gson.annotations.SerializedName

data class Category(
    val category: String,
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("sub_category")
    val subCategory: List<SubCategory>
)