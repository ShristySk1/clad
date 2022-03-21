package com.ayata.clad.home.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Coupon(
    val code: String,
    val description: String,
    @SerializedName("discount_amount")
    val discountAmount: Double,
    val id: Int,
    val title: String
):Serializable