package com.ayata.clad.productlist.response


import com.google.gson.annotations.SerializedName

data class Color(
    @SerializedName("hex_value")
    val hexValue: String,
    val id: Int,
    val name: String
)