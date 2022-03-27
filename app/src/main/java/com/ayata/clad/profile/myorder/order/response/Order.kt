package com.ayata.clad.profile.myorder.order.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Order(
    val code: String,
    @SerializedName("contact_number")
    val contactNumber: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("dollar_total")
    val dollarTotal: Double,
    @SerializedName("npr_total")
    val nprTotal: Double,
    @SerializedName("order_id")
    val orderId: Int,
    @SerializedName("order_status")
    val orderStatus: List<OrderStatus>,
    val products: Products,
    @SerializedName("receiver_name")
    val receiverName: String,
    @SerializedName("shipping_address")
    val shippingAddress: String
):Serializable