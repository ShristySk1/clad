package com.ayata.clad.shop.response


import com.google.gson.annotations.SerializedName

data class Category(
    val title: String,
    val id: Int,
    val slug:String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("sub_category")
    val subCategory: List<SubCategory>
)