package com.ayata.clad.profile.myorder.order.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Detail(
    @SerializedName("order_date")
    val orderDate: String,
    val orders: List<Order>
):Serializable