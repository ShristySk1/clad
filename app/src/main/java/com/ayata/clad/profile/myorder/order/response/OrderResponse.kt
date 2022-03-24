package com.ayata.clad.profile.myorder.order.response


import com.google.gson.annotations.SerializedName

data class OrderResponse(
    val details: List<Detail>
)