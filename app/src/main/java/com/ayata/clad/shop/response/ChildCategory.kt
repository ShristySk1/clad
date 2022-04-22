package com.ayata.clad.shop.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ChildCategory(
    val id: Int,
    @SerializedName("image_url")
    val image: String,
    val title: String,
    val slug:String,
    val comment: String = "",
    val product_count:Int=0
) : Serializable