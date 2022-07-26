package com.ayata.clad.shopping_bag.response.checkout


import com.ayata.clad.home.response.ProductDetail
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Cart(
    @SerializedName("cart_id")
    val cartId: Int,
    val selected: Selected,
    val is_selected:Boolean
):Serializable