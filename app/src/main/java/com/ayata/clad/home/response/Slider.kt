package com.ayata.clad.home.response


import com.google.gson.annotations.SerializedName

data class Slider(
    val id: Int,
    @SerializedName("image_url")
    val imageUrl: String,
    val title: String
)