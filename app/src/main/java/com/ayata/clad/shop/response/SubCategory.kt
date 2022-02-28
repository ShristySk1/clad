package com.ayata.clad.shop.response


import com.google.gson.annotations.SerializedName

data class SubCategory(
    @SerializedName("child_categories")
    val childCategories: List<ChildCategory>,
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String,
    val title: String
)