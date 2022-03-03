package com.ayata.clad.shop.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SubCategory(
    @SerializedName("child_categories")
    val childCategories: List<ChildCategory>,
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("comment")
    val comment: String="",
    val title: String
):Serializable