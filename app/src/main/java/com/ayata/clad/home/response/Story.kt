package com.ayata.clad.home.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Story(
    val contents: List<Content>,
    val vendor: String
):Serializable