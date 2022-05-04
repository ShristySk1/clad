package com.ayata.clad.profile.myorder.order.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrderResponse(
    @SerializedName("orders")
    val details: List<Detail>
):Serializable