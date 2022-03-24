package com.ayata.clad.profile.myorder.order.response


import com.google.gson.annotations.SerializedName

data class OrderStatus(
    val date: String,
    val status: String
)