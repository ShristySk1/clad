package com.ayata.clad.home.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Brand(
    @SerializedName("brand_image")
    val brandImage: String,
    val id: Int,
    val name: String,
    val slug:String
):Serializable