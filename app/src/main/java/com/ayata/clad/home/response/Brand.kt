package com.ayata.clad.home.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Brand(
    @SerializedName("icon_url")
    val iconUrl: String,
    val id: Int,
    val name: String
):Serializable