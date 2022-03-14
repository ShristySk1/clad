package com.ayata.clad.shopping_bag.response.checkout


import com.ayata.clad.home.response.ProductDetail
import com.google.gson.annotations.SerializedName

data class Cart(
    @SerializedName("cart_id")
    val cartId: Int,
    @SerializedName("product_details")
    val productDetails: ProductDetail,
    val selected: Selected,
    val is_selected:Boolean
)