package com.ayata.clad.home.response


import com.google.gson.annotations.SerializedName

data class Query(
    val answer: String,
    @SerializedName("posted_at")
    val postedAt: String,
    @SerializedName("posted_by")
    val postedBy: String,
    val question: String
)