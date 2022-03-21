package com.ayata.clad.home.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Tag(
    val description: String,
    val id: Int,
    val title: String
):Serializable