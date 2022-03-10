package com.ayata.clad.shopping_bag.response.checkout


import com.google.gson.annotations.SerializedName

data class Tag(
    val description: String,
    val id: Int,
    val title: String
)