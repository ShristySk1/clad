package com.ayata.clad.shopping_bag.response.checkout


import com.google.gson.annotations.SerializedName

data class Brand(
    @SerializedName("icon_url")
    val iconUrl: String,
    val id: Int,
    val name: String
)