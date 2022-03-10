package com.ayata.clad.shopping_bag.response.checkout


import com.google.gson.annotations.SerializedName

data class CartResponse(
    val cart: List<Cart>,
    @SerializedName("cart_total_dollar")
    val cartTotalDollar: Double,
    @SerializedName("cart_total_npr")
    val cartTotalNpr: Double
)